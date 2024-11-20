# User Management Application

This is a Spring Boot-based User Management application that allows you to manage users via a REST API. The application integrates with PostgreSQL for database storage and Redis for caching.

## Key Features
- **User Management**: Create, read, update, and delete user records.
- **Admin-Only Access**: Admin can access stats and all users' data.
- **Database Integration**: PostgreSQL is used to persist user data.
- **Caching**: Redis is utilized to cache frequently accessed data for improved performance.
- **Security**: Secure API endpoints with token-based authentication (JWT).
- **API Documentation**: Comprehensive endpoints to manage user-related operations.


## How to Run the Application

### Prerequisites
- **Docker**: Docker must be installed on your machine to run the application in containers.
- **Postman** or **cURL**: You can use Postman or any HTTP client like cURL to make requests.

### Steps to Run

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/RazElbaz/User-Management-Application.git
   cd User-Management-Application/user-management
   ```

2. **Build and Start the Application using Docker**:
   Make sure you have Docker installed. To start the application with the necessary services (PostgreSQL, Redis, and the app itself), run:
   ```bash
   docker-compose up --build
   ```
   This will:
    - Build the Spring Boot application Docker image.
    - Start the **app**, **db** (PostgreSQL), and **redis** services.

3. **Access the Application**:
   The application will be running at [http://localhost:8080](http://localhost:8080).
---
### How to Run the Application Locally Without Docker Compose

If you prefer to run the application locally without using `docker-compose`, you can set up the required services manually using Docker commands:

1. **Run Redis**:
   ```bash
   docker run --name redis -d -p 6379:6379 redis
   ```

2. **Run PostgreSQL**:
   ```bash
   docker run --name postgres-container -e POSTGRES_USER=postgresql -e POSTGRES_PASSWORD=postgresql_password -e POSTGRES_DB=users -p 5432:5432 -d postgres
   ```

3. **Start the Spring Boot Application**:
   With Redis and PostgreSQL running, start the Spring Boot application locally by running:
   ```bash
   ./mvnw spring-boot:run
   ```
   Ensure your `application.properties` or environment variables are configured to connect to the locally running Redis and PostgreSQL instances.
---

### Tests

The project includes unit tests to ensure the reliability of key services:

1. **Statistics Service Test**:
    - Location: `src/test/java/com/example/usermanagement/StatisticsServiceTest.java`
    - Description: Tests the functionality of the statistics service, including user analytics and performance metrics.

2. **User Service Test**:
    - Location: `src/test/java/com/example/usermanagement/UserServiceTest.java`
    - Description: Validates user-related operations such as creation, updates, and retrieval.

---

## API Endpoints

### 1. **Sign Up a New User**

- **URL**: `/api/signUp`
- **Method**: `POST`
- **Body**: JSON format
- **Description**: Registers a new user. Anyone can use this endpoint to register a new user.

**Example Request**:
```json
{
  "name": "John Doe",
  "email": "johndoe@example.com",
  "password": "123456Jd!"
}
```

**Response**:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "johndoe@example.com",
  "status": "ACTIVE",
  "role": "USER",
  "authorities": [
    {
      "authority": "ROLE_USER"
    }
  ]
}
```


---

### 2. **Login (Obtain JWT Token)**

- **URL**: `/api/login`
- **Method**: `POST`
- **Query Parameters**: `email`, `password`
- **Description**: Logs in and returns a JWT token. Admin is created automatically upon initial setup.

**Example Request**:
```
POST /api/login?email=admin@domain.com&password=adminPassword
```

**Response**:
```json
{
  "user": {
    "id": 1,
    "name": "Admin User",
    "email": "admin@example.com",
    "status": "ACTIVE",
    "role": "ADMIN",
    "authorities": [
      {
        "authority": "ROLE_USER"
      },
      {
        "authority": "ROLE_ADMIN"
      }
    ]
  },
  "token": "<your_jwt_token>"
}
```

Use the received JWT token in the **Authorization** header as a **Bearer token** for accessing admin routes.


**Notes**:
- The **JWT Token** (`<your_jwt_token>`) is returned in the `token` field and must be included in the `Authorization` header (with the prefix `Bearer`) for subsequent requests requiring admin access.
---

### 3. **Get User Stats (Admin Only)**

- **URL**: `/api/stats`
- **Method**: `GET`
- **Description**: Fetches statistics about the users (Admin only). Requires JWT token.


**Example Request**:
```
GET /api/stats
```

**Headers**:
- `Authorization: Bearer <your_jwt_token>`

**Response**:
```json
{
  "totalUsers": 100,
  "totalRequests": 200,
  "averageRequestTime": 300
}
```

---

### 4. **Get All Users (Admin Only)**

- **URL**: `/api/getAllUsers`
- **Method**: `GET`
- **Description**: Retrieves a list of all users (Admin only). Requires JWT token.

**Example Request**:
```
GET /api/getAllUsers
```

**Headers**:
- `Authorization: Bearer <your_jwt_token>`

**Response**:
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "johndoe@example.com",
    "status": "active"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "janesmith@example.com",
    "status": "inactive"
  }
]
```

---



### 5. **Update User Status**

- **URL**: `/api/updateStatus`
- **Method**: `PUT`
- **Query Parameters**: `id`, `status`
- **Description**: Updates the status of a user to either **active** or **inactive**.

**Example Request**:
```
PUT /api/updateStatus?id=39&status=inactive
```

**Response**:
```json
{
  "id": 39,
  "name": "John Doe",
  "email": "johndoe@example.com",
  "status": "inactive"
}
```


---

- **URL**: `/api/deleteUser`
- **Method**: `DELETE`
- **Query Parameters**: `id`
- **Description**: Deletes a user by ID and returns a confirmation message.

**Example Request**:
```
DELETE /api/deleteUser?id=30
```

**Response**:
```json
{
  "message": "User deleted successfully"
}
```
---

## Authentication and Authorization

- **JWT Authentication**: After logging in with the `/api/login` endpoint, the user will receive a JWT token.
- **Admin Access**: The admin can access restricted endpoints (`/api/stats`, `/api/getAllUsers`) using the token. Only the admin account (created automatically on first run) has access to these routes.
- **Forbidden for Non-Admins**: Regular users will receive a `403 Forbidden` error if they try to access admin-only endpoints such as `/api/stats` or `/api/getAllUsers`.

---
