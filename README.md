# WebFlux Task Manager

**WebFlux Task Manager** is a reactive task tracking application built using **Spring Boot**, **Spring WebFlux**, and **MongoDB Atlas**. The application demonstrates reactive programming by using **MongoDB Reactive** for handling task data, along with a secure and scalable backend.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [License](#license)

---

## Features

- **Reactive Programming** using **Spring WebFlux**.
- **MongoDB Atlas** as a reactive database.
- **CRUD Operations**: Create, Read, Update, and Delete tasks.
- **Validation** of task data using `javax.validation`.
- **Security**: Spring Security integration for role-based access control.
- **Thymeleaf** templates for basic UI (optional).
- **Monitoring**: Spring Boot Actuator endpoints for health checks and metrics.

---

## Technologies

The following technologies and dependencies are used in this project:

- **Java 22**
- **Spring Boot 3.4**
- **Spring WebFlux** for reactive web framework.
- **Spring Data MongoDB Reactive** for reactive data handling.
- **Spring Security** for authentication and authorization.
- **MongoDB Atlas** for cloud-hosted NoSQL database.
- **Lombok** to reduce boilerplate code.
- **Spring Boot DevTools** for hot reload during development.
- **Spring Boot Actuator** for monitoring and metrics.
- **H2 Database** (optional, for testing).

---

## Prerequisites

Before running this application, ensure you have:

- **Java 22** or higher installed.
- A **MongoDB Atlas** account (or a local MongoDB instance).
- **Maven** installed to manage project dependencies.

---

## Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/WebFluxTaskManager.git
   cd WebFluxTaskManager
    ```
2. **Configure MongoDB Atlas**:
    - Create a **MongoDB Atlas** cluster if you haven't already.
    - Whitelist your IP address in MongoDB Atlas or use `0.0.0.0/0` for development purposes (Note: using `0.0.0.0/0` is not recommended for production environments as it allows access from any IP).
    - Create a **database user** in Atlas and assign appropriate roles (readWrite access is recommended for development).
    - Obtain the **connection string** from MongoDB Atlas by selecting **Connect** > **Connect your application**. Choose **Java** and copy the connection string in the format:
      ```
      mongodb+srv://<username>:<password>@cluster0.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
      ```
    - Replace `<username>`, `<password>`, and `myFirstDatabase` with your own database credentials and the name of the database you want to use.


3. **Set environment variables**:
   Set the following environment variables in your system to avoid hardcoding sensitive information:

    - `MONGO_DB_USER`: Your MongoDB Atlas username.
    - `MONGO_DB_PASSWORD`: Your MongoDB Atlas password.
    - `MONGO_DB_HOST`: Your MongoDB Atlas cluster host (e.g., `cluster0.mongodb.net`).
    - `MONGO_DB_NAME`: The name of your database.


---

## Running the Application

1. **Run the application locally**:
   Use the following Maven command to run the application:
   ```bash
    ./mvnw spring-boot:run
   ```

2. **Access the application**:
   Once the application is running, you can access the Task Tracker API at `http://localhost:8080`.

---

## API Endpoints

The following API endpoints are available for managing tasks:

| Method | Endpoint         | Description                         |
|--------|------------------|-------------------------------------|
| GET    | `/tasks`          | Retrieve a list of all tasks        |
| GET    | `/tasks/{id}`     | Retrieve a specific task by ID      |
| POST   | `/tasks`          | Create a new task                   |
| PUT    | `/tasks/{id}`     | Update an existing task by ID       |
| DELETE | `/tasks/{id}`     | Delete a task by ID                 |

### Example request (creating a task):

`POST /tasks`

**Headers**: `Content-Type: application/json`

**Body**:
```json
{
  "title": "Complete WebFlux Project",
  "description": "Finish the WebFlux Task Tracker application",
  "status": "IN_PROGRESS",
  "dueDate": "2024-10-01"
}
```

### Example response (getting all tasks):

`GET /tasks`

**Response**:
```json
[
  {
    "id": "60f7b3f0b9b2e209d4c7f0f5",
    "title": "Complete WebFlux Project",
    "description": "Finish the WebFlux Task Tracker application",
    "status": "IN_PROGRESS",
    "dueDate": "2024-10-01"
  },
  {
    "id": "60f7b3f0b9b2e209d4c7f0f6",
    "title": "Review Documentation",
    "description": "Review the project documentation and update it",
    "status": "PENDING",
    "dueDate": "2024-09-28"
  }
]
```

---

## Testing

To run the tests for the application, use the following command:
- `./mvnw test`

### Test Coverage:

- **Unit Tests**: Tests for services and controllers.
- **Integration Tests**: End-to-end tests that ensure the correct functioning of the entire application.
- **Security Tests**: Tests for authentication and authorization functionality using Spring Security.

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

---

## Additional Notes

- If you are running the application in **Docker** or another containerized environment, make sure to configure the environment variables (`MONGO_DB_USER`, `MONGO_DB_PASSWORD`, `MONGO_DB_HOST`, and `MONGO_DB_NAME`) within the container setup.

- For **production environments**, ensure that sensitive credentials are securely stored, and avoid exposing them in version control or public configuration files.