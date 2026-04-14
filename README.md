RunTracker is a REST API built with Java and Spring Boot for tracking running activities.
The project focuses on learning and applying backend concepts such as API design that supports pagination, filtering, batch operations, and partial updates (PATCH), database integration, authentication, and automated testing.

This project is currently in active development and is now moving into integration with a Discord bot.

**Setup & Configuration**

This project requires local configuration before running.

1. Clone the Repository
git clone https://github.com/your-username/runtracker.git
cd runtracker

3. Configure Application Properties
Update application.properties or application.yml with your local database credentials:

spring.datasource.url=jdbc:your_database_url
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

Adjust values based on your database setup.

3. Build and Run
mvn clean install
mvn spring-boot:run

The API should start on:

http://localhost:8080

**Testing with Postman**

Currently, the API is tested locally using Postman.

Steps:

Open Postman

Create a new request

Set the request URL (example):

http://localhost:8080/api/runs

Select the HTTP method (GET, POST, PUT, DELETE)

Add required headers and body

Send the request


## Example Endpoints

### 👤 User Endpoints

#### Get Users (with filtering & pagination)
GET http://localhost:8080/users

Optional query params:
GET /users?page=0&size=10

---

#### Get User Stats
GET http://localhost:8080/users/{id}/stats

---

#### Create User
POST http://localhost:8080/users

Example body:
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```
## 🏃 Run Endpoints

#### Get Runs (with filtering & pagination)
GET http://localhost:8080/runs

Example with filter:
GET /runs?minDistance=5&page=0&size=10

---

#### Create Run
POST http://localhost:8080/runs

Example body:
```json
{
  "distance": 5.0,
  "duration": 30,
  "date": "2026-04-14"
}
```

