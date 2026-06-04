# RunTracker

RunTracker is a backend-focused portfolio project for tracking running activity through a Spring Boot REST API and a Discord bot client. The project demonstrates practical API design, secure configuration, database integration, bot-to-API authentication, and user-facing command flows.


- **REST API design:** user and run resources, filtering, pagination, partial updates, batch-style operations, and stats endpoints.
- **Discord integration:** a Node.js bot lets users register, link accounts, log runs, and view stats without leaving Discord.
- **Security-minded configuration:** credentials and bot API keys are loaded from environment variables instead of being committed to source control.
- **Database-backed persistence:** Spring Data JPA with PostgreSQL for local development and H2 for tests.
- **Automation-friendly setup:** Gradle and npm scripts make it easy to validate the Java API and Discord bot.

## Tech stack

| Area | Tools |
| --- | --- |
| Backend API | Java 21, Spring Boot, Spring Web, Spring Security |
| Persistence | Spring Data JPA, PostgreSQL, H2 for tests |
| Discord bot | Node.js, discord.js, axios, dotenv |
| Build/test | Gradle Wrapper, npm |

## Project structure

```text
.
├── build.gradle                 # Spring Boot dependencies and test configuration
├── src/main/resources/          # API configuration
└── discord-bot/                 # Discord bot client and npm project
```

## Local setup

### 1. Configure the Spring Boot API

Create local environment variables for the database, Spring Security user, and bot shared secret:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/running_tracker"
export DB_USERNAME="postgres"
export DB_PASSWORD="your-password"
export SPRING_SECURITY_USER="sam"
export SPRING_SECURITY_PASSWORD="your-local-password"
export BOT_API_KEY="replace-with-a-long-random-secret"
```

Then run the API:

```bash
./gradlew bootRun
```

The API starts on `http://localhost:8080` by default.

### 2. Configure the Discord bot

```bash
cd discord-bot
cp .env.example .env
npm install
npm start
```

Update `.env` with your Discord token, the API URL, and the same `BOT_API_KEY` value used by the Spring Boot API.

## Discord bot commands

| Command | Description |
| --- | --- |
| `!help` | Show available commands. |
| `!register <username>` | Create and link a RunTracker account. |
| `!createuser <username>` | Alias for `!register`. |
| `!link <userId>` | Link Discord to an existing RunTracker user. |
| `!logrun <distanceKm> <durationMinutes>` | Log a run, for example `!logrun 5 30` or `!logrun 5km 30min`. |
| `!stats` | Show total, weekly, monthly, yearly, longest-run, and fastest-run stats. |
| `!unlink` | Remove the Discord account link. |
| `!ping` | Confirm the bot is online. |

#### Example bot usage 
<img width="814" height="694" alt="image" src="https://github.com/user-attachments/assets/d6ec9a14-26bd-44ab-a496-677d1ee070fc" />


## Example API endpoints

### User endpoints

```http
GET /users?page=0&size=10
GET /users/{id}/stats
POST /users
```

Example create-user body:

```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

### Run endpoints

```http
GET /runs?minDistance=5&page=0&size=10
POST /runs
```

Example create-run body:

```json
{
  "distance": 5.0,
  "duration": 30,
  "date": "2026-04-14"
}
```

## Testing and validation

```bash
./gradlew test
cd discord-bot && npm test
```

