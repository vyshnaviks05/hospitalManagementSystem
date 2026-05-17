<div align="center">

# 🏥 Hospital Management System

A secure RESTful backend for managing hospital operations — patients, doctors, appointments, departments, and insurance — built with Java 21 and Spring Boot 3.

Secured using JWT-based authentication and role-based access control with a layered backend architecture focused on clean API design, validation, transaction management, and JPA/Hibernate best practices.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?style=flat-square&logo=postgresql)
![JWT](https://img.shields.io/badge/Auth-JWT-black?style=flat-square&logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Build-Maven-red?style=flat-square&logo=apachemaven)

</div>

---

## Key Concepts Demonstrated

- JWT Authentication & Authorization
- Role-Based Access Control (RBAC)
- DTO-Based API Architecture
- Layered Architecture (Controller → Service → Repository)
- JPA/Hibernate Entity Relationships
- Lazy Loading & Fetch Joins
- Transaction Management
- Centralized Exception Handling
- Bean Validation
- Pagination & Search APIs
- Environment-Based Secret Management

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3, Spring MVC, Spring Security |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Validation | Jakarta Bean Validation |
| Boilerplate Reduction | Lombok |
| Build Tool | Maven |
| Testing | Spring Boot Test (JUnit 5) |

---

## Features

- JWT stateless authentication with role-based access control (`ADMIN`, `DOCTOR`, `PATIENT`)
- Full CRUD for patients and doctors with pagination and search
- Appointment scheduling with doctor-level conflict detection
- Appointment status transitions (`SCHEDULED → CANCELLED / COMPLETED`)
- Doctor reassignment for existing appointments
- Patient insurance assignment and removal
- Centralized exception handling with structured field-level validation responses
- BCrypt password encoding — plain-text passwords are never stored
- Database credentials and JWT secrets externalized via environment variables

---

## Architecture

The application follows a standard layered backend architecture:

```text
Client
  ↓
JWT Filter
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
PostgreSQL
```

Input and output are decoupled from JPA entities using dedicated DTOs and manual mapper classes.

All exceptions are handled centrally using `@RestControllerAdvice`.

### Project Structure

```text
src/main/java/com/vyshnavi/dev/hospitalManagement/
├── controller/       # REST endpoints
├── security/         # JwtFilter, SecurityConfig, UserDetailsServiceImpl
├── service/          # Business logic & transaction management
├── repository/       # Spring Data JPA repositories
├── entity/           # JPA entities & relationships
├── dto/              # Request / response DTOs
├── mapper/           # DTO ↔ Entity conversion
├── exception/        # Custom exceptions & handlers
└── config/           # Configuration classes
```

---

## Entity Relationships

```text
Patient     ──(1:1)──  Insurance
Patient     ──(1:N)──  Appointment  ──(N:1)──  Doctor
Department  ──(M:N)──  Doctor
```

---

## API Reference

| Domain | Base Path |
|---|---|
| Auth | `/api/auth` |
| Patients | `/api/patients` |
| Doctors | `/api/doctors` |
| Appointments | `/api/appointments` |
| Insurance | `/api/patients/{patientId}/insurance` |

---

## Authentication

```http
# Register — public endpoint, always creates a PATIENT account
POST /api/auth/register
{
  "username": "john",
  "password": "secret123"
}

# Login — returns a signed JWT token
POST /api/auth/login
{
  "username": "john",
  "password": "secret123"
}

# Use token in all subsequent requests
Authorization: Bearer <token>
```

> Public registration is intentionally restricted to the `PATIENT` role. `ADMIN` and `DOCTOR` accounts are provisioned separately through seed data to prevent unauthorized privilege escalation through public registration.

### Access Matrix

| Role | Access |
|---|---|
| ADMIN | All endpoints |
| DOCTOR | Patients, Doctors, Appointments |
| PATIENT | Patients, Appointments |

---

## Technical Highlights

### Conflict Detection

`AppointmentService` checks:

```java
existsByDoctorIdAndAppointmentTime()
```

before booking appointments to prevent double booking for the same doctor and time slot.

---

### N+1 Query Prevention

`LEFT JOIN FETCH` is used in repository queries such as:

```java
findAllPatientsWithAppointments()
```

to load related entities in a single SQL query and avoid N+1 query issues caused by lazy loading.

---

### Hibernate Dirty Checking

Update methods (`updatePatient`, `updateDoctor`) do not explicitly call `save()`.

Entities are loaded inside a transactional context, modified via setters, and Hibernate automatically detects changes and generates the required `UPDATE` SQL during transaction commit.

---

### Transaction Management

- `@Transactional(readOnly = true)` is used for read operations to optimize transaction behavior and avoid unnecessary flushes
- `@Transactional` is used for write operations to ensure rollback on failure

---

### Input Validation

Jakarta Bean Validation annotations such as:

```java
@NotBlank
@Email
@Past
@NotNull
```

are applied to request DTOs with structured field-level validation responses handled via:

```java
MethodArgumentNotValidException
```

---

### Externalized Secrets

Database credentials and JWT secrets are read from environment variables:

```text
DB_PASSWORD
JWT_SECRET
```

No secrets are hardcoded in the application.

---

## Getting Started

### Prerequisites

- Java 21+
- PostgreSQL
- Maven

---

### 1. Clone the Repository

```bash
git clone https://github.com/vyshnaviks05/hospitalManagementSystem.git
cd hospitalManagementSystem
```

---

### 2. Create the Database

```sql
CREATE DATABASE hospitalDB;
```

---

### 3. Set Environment Variables

```bash
# macOS / Linux
export DB_PASSWORD=your_postgres_password
export JWT_SECRET=your_jwt_secret_minimum_32_characters

# Windows
set DB_PASSWORD=your_postgres_password
set JWT_SECRET=your_jwt_secret_minimum_32_characters
```

---

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

Application starts at:

```text
http://localhost:8080
```

Seed data (`5 patients`, `3 doctors`, `6 appointments`) loads automatically via `data.sql`.

> ⚠️ `spring.jpa.hibernate.ddl-auto=create` is intentionally configured for local development and recreates the schema on startup. Use `update`, `validate`, or database migrations before deploying to shared or production environments.

---

## Tests

```bash
./mvnw test
```

| Test Class | Coverage |
|---|---|
| `PatientTests` | Paginated fetch, search functionality |
| `InsuranceTests` | Insurance assignment/removal, appointment workflows |
| `HospitalManagementApplicationTests` | Application context loading |

---

## Roadmap

- [x] JWT authentication & role-based authorization
- [x] Appointment conflict detection
- [x] Structured exception handling
- [x] Pagination & search endpoints
- [ ] Unit tests for service layer using Mockito
- [ ] Swagger / OpenAPI documentation
- [ ] Refresh token implementation
- [ ] Redis caching
- [ ] Flyway database migrations
- [ ] Docker support

---

## Author

Kotha Sree Vyshnavi · [Email](mailto:vyshukotha05@gmail.com) · [LinkedIn](https://www.linkedin.com/in/kotha-sree-vyshnavi-438736277/) · [GitHub](https://github.com/vyshnaviks05)

