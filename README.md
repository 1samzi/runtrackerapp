# 🏃 RunTracker API

RunTracker is a work-in-progress REST API built with Java and Spring Boot for tracking running activities.
The project focuses on learning and applying backend concepts such as API design, database integration, authentication, and automated testing.

This project is currently in active development (Week 6) and is planned to be integrated with a Discord bot in the future.

### 📌 Project Status

##### 🚧 In Progress

Core REST endpoints implemented

Database integration in place

Basic authentication and filtering

Automated tests included

Planned Discord bot integration (future)

Some features and deployment options are still under development.

### 🚀 Features

Create, read, update, and delete run records

RESTful API design using Spring Boot

Database persistence using JPA/Hibernate

Authentication and authorization

Filtering and querying endpoints

Unit and integration testing

### 🛠️ Tech Stack

Language: Java

Framework: Spring Boot

Database: JPA / Hibernate (configurable)

Build Tool: Maven

Testing: JUnit / Spring Test

API Testing: Postman

### 📂 Prerequisites

Before running this project, make sure you have:

Java 17+ (or your project’s version)

Maven

A configured local database (MySQL/PostgreSQL/H2, depending on setup)

Postman (for API testing)

### ⚙️ Setup & Configuration

This project requires local configuration before running.

#### 1. Clone the Repository
git clone https://github.com/your-username/runtracker.git
cd runtracker

#### 2. Configure Application Properties

Update application.properties or application.yml with your local database credentials:

spring.datasource.url=jdbc:your_database_url
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update


Adjust values based on your database setup.

#### 3. Build and Run
mvn clean install
mvn spring-boot:run


The API should start on:

http://localhost:8080

🧪 Testing with Postman

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

### 📘 Example Endpoints

Actual endpoints may change as development continues.

#### Method	Endpoint	Description
GET	http://localhost:8080/runs	Get all runs
POST	http://localhost:8080/runs	Create a new run
GET	http://localhost:8080/runs/{id}	Get run by ID
PUT	http://localhost:8080/runs/{id}	Update run
DELETE	http://localhost:8080/runs/{id}	Delete run

### 🤖 Planned Features

Discord bot integration

Cloud deployment

User profiles

Statistics and analytics

Improved authentication

Public API documentation (Swagger/OpenAPI)

### 📚 Learning Objectives

This project is designed to strengthen understanding of:

REST API development

Backend architecture

Database modeling

Authentication systems

Testing practices

API consumption by external services (Discord bot)

🤝 Contributing

This is currently a personal learning project.
Suggestions and feedback are welcome.

