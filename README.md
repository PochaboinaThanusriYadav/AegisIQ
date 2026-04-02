# AegisIQ

AegisIQ is a full-stack incident management and risk analysis application.
It combines a Spring Boot backend with a React + Vite frontend to support
authentication, incident reporting, incident tracking, admin actions, and
dashboard views.

## Tech Stack

- Backend: Java 21, Spring Boot, Spring Web, Spring Security, Spring Data JPA
- Database: MySQL
- Frontend: React 18, Vite, React Router, Axios
- Testing: Spring Boot Test, H2 for test profile

## Features

- User login and registration
- Role-based route protection
- Incident reporting and incident details view
- Dashboard for users and admins
- Emergency contact and response support
- AI-assisted risk analysis service

## Project Structure

- `src/main/java/com/example/AegisIQ` - Spring Boot backend source
- `src/main/resources` - Backend configuration
- `src/test` - Backend tests and test resources
- `frontend` - React application

## Prerequisites

- JDK 21
- Maven
- Node.js 18+ and npm
- MySQL database access

## Backend Setup

The backend reads the database password from the `DB_PASSWORD` environment
variable.

Set it before starting the application:

```powershell
$env:DB_PASSWORD="your-database-password"
```

Run the backend:

```powershell
./mvnw spring-boot:run
```

The backend starts on port `8080`.

## Frontend Setup

Install dependencies:

```powershell
cd frontend
npm install
```

Run the frontend development server:

```powershell
npm run dev
```

The frontend runs on port `3000` and proxies API requests to the backend at
`http://localhost:8080`.

## Build

Backend build:

```powershell
./mvnw clean package
```

Frontend build:

```powershell
cd frontend
npm run build
```

## Deployment

The repository now includes a Dockerfile for a single-service deployment.
The frontend is built into the Spring Boot application, so the backend serves
the React app and the API from the same origin.

### Render

1. Create a new Web Service from this repository.
2. Use the provided `Dockerfile`.
3. Set `DB_PASSWORD` in the Render environment variables.
4. Deploy the service.

The application will run on port `8080` inside Render.

### Local Docker Build

```powershell
docker build -t aegisiq .
docker run -p 8080:8080 -e DB_PASSWORD="your-database-password" aegisiq
```

## Notes

- Do not commit real database credentials to the repository.
- The frontend stores the current user in local storage for route protection.
- Generated folders such as `target/`, `frontend/dist/`, and
  `frontend/node_modules/` should stay out of version control.