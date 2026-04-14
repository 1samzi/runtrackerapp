RunTracker is a REST API built with Java and Spring Boot for tracking running activities.
The project focuses on learning and applying backend concepts such as API design, database integration, authentication, and automated testing.

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

Authentication may be required depending on the endpoint.

📘 Example Endpoints

Actual endpoints may change as development continues.

Method	Endpoint	Description
GET	http://localhost:8080/runs
	Get all runs
POST	http://localhost:8080/runs
	Create a new run
GET	http://localhost:8080/runs/{id}
	Get run by ID
PUT	http://localhost:8080/runs/{id}
	Update run
DELETE	http://localhost:8080/runs/{id}
	Delete run
