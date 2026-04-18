<div align="center">

# 🏥 Hospital Management System

A RESTful backend for managing hospital operations — patients, doctors, appointments, departments, and insurance — built with Java 21 and Spring Boot 3. Secured with JWT-based authentication and role-based access control.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?style=flat-square&logo=postgresql)
![JWT](https://img.shields.io/badge/Auth-JWT-black?style=flat-square&logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Build-Maven-red?style=flat-square&logo=apachemaven)

</div>

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
| Boilerplate | Lombok |
| Build | Maven |
| Testing | Spring Boot Test (JUnit 5) |

---

## Features

- JWT stateless authentication with role-based access control (`ADMIN`, `DOCTOR`, `PATIENT`)
- Full CRUD for patients and doctors
- Appointment scheduling and doctor reassignment
- Patient insurance assignment and removal
- Centralized exception handling with structured field-level error responses
- BCrypt password encoding — plain-text passwords are never stored

---

## Architecture

Standard layered architecture — Controller → Service → Repository. Input and output are decoupled from JPA entities using dedicated DTOs with manual mapper classes. All exceptions are handled centrally via `@RestControllerAdvice`.

```
src/main/java/com/vyshnavi/dev/hospitalManagement/
├── controller/       # REST endpoints, GlobalExceptionHandler
├── security/         # JwtFilter, SecurityConfig, UserDetailsServiceImpl
├── service/          # Business logic
├── repository/       # JPQL, native SQL, pagination, bulk updates, fetch joins
├── entity/           # JPA entities
├── dto/              # Request / response DTOs
├── mapper/           # DTO ↔ Entity conversion
└── exception/        # ResourceNotFoundException
```

---

## Entity Relationships

```
Patient     ──(1:1)──  Insurance
Patient     ──(1:N)──  Appointment  ──(N:1)──  Doctor
Department  ──(M:N)──  Doctor
Department  ──(1:1)──  Doctor  (head)
```

---

## API Reference

<details>
<summary><strong>Auth</strong> — <code>/api/auth</code></summary>
<br>

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/register` | Register a new user |
| `POST` | `/login` | Login and receive JWT token |

</details>

<details>
<summary><strong>Patients</strong> — <code>/api/patients</code></summary>
<br>

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Create a new patient |
| `GET` | `/` | Retrieve all patients |
| `GET` | `/{id}` | Retrieve a patient by ID |
| `PUT` | `/{id}` | Update patient details |
| `DELETE` | `/{id}` | Delete a patient |

</details>

<details>
<summary><strong>Doctors</strong> — <code>/api/doctors</code></summary>
<br>

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Create a new doctor |
| `GET` | `/` | Retrieve all doctors |
| `GET` | `/{id}` | Retrieve a doctor by ID |
| `PUT` | `/{id}` | Update doctor details |
| `DELETE` | `/{id}` | Delete a doctor |

</details>

<details>
<summary><strong>Appointments</strong> — <code>/api/appointments</code></summary>
<br>

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Create a new appointment |
| `PATCH` | `/{id}/reassign?doctorId={id}` | Reassign appointment to another doctor |

</details>

<details>
<summary><strong>Insurance</strong> — <code>/api/patients/{patientId}/insurance</code></summary>
<br>

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Assign insurance to a patient |
| `DELETE` | `/` | Remove insurance from a patient |

</details>

---

## Authentication

```bash
# Register
POST /api/auth/register
{ "username": "doctor1", "password": "1234", "role": "DOCTOR" }

# Login — returns a signed JWT token
POST /api/auth/login
{ "username": "doctor1", "password": "1234" }

# Use token in all subsequent requests
Authorization: Bearer <token>
```

| Role | Access |
|------|--------|
| `ADMIN` | All endpoints |
| `DOCTOR` | Patients, Doctors, Appointments |
| `PATIENT` | Patients, Appointments |

---

## Technical Highlights

- **N+1 prevention** — `LEFT JOIN FETCH` in `findAllPatientsWithAppointments()` loads patients and appointments in a single SQL query instead of one query per patient
- **Transaction management** — `@Transactional(readOnly = true)` on all read operations disables Hibernate dirty checking; write operations use `@Transactional` for automatic rollback on failure
- **8 query patterns** in `PatientRepository` — derived methods, JPQL, DTO projections, native SQL with pagination, bulk updates, and fetch joins
- **Input validation** — Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Past`, `@Future`, `@NotNull`) on all request DTOs with structured field-level error responses
- **Externalized secrets** — database password is read from `${DB_PASSWORD}` environment variable; never hardcoded

---

## Getting Started

**Prerequisites:** Java 21+, PostgreSQL, Maven

**1. Clone the repository**
```bash
git clone https://github.com/vyshnaviks05/hospitalManagementSystem.git
cd hospitalManagementSystem
```

**2. Create the database**
```sql
CREATE DATABASE hospitalDB;
```

**3. Set the environment variable**
```bash
# macOS / Linux
export DB_PASSWORD=your_postgres_password

# Windows
set DB_PASSWORD=your_postgres_password
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

Server starts at `http://localhost:8080`. Seed data (5 patients, 3 doctors, 6 appointments) loads automatically via `data.sql`.

> **⚠️ Note:** `spring.jpa.hibernate.ddl-auto=create` drops and recreates the schema on every startup. Switch to `update` or `validate` before deploying to staging or production.

---

## Tests

```bash
./mvnw test
```

| Test Class | Coverage |
|------------|----------|
| `PatientTests` | Fetch join query, getPatientById, paginated native query with sorting, ResourceNotFoundException for missing patient |
| `InsuranceTests` | Assign insurance, remove insurance, create appointment, reassign appointment to another doctor |
| `HospitalManagementApplicationTests` | Application context loads successfully |

---

## Roadmap

- [x] Spring Security with JWT authentication and role-based access control (ADMIN, DOCTOR, PATIENT)
- [ ] Unit tests for service layer using Mockito
- [ ] Automated DTO mapping with MapStruct
- [ ] Redis caching for frequently accessed data
- [ ] Swagger / OpenAPI documentation

---

<div align="center">

**Kotha Sree Vyshnavi**

[![Email](https://img.shields.io/badge/Email-D14836?style=flat-square&logo=gmail&logoColor=white)](mailto:vyshukotha05@gmail.com)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat-square&logo=linkedin&logoColor=white)](https://linkedin.com)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/vyshnaviks05)


</div>
