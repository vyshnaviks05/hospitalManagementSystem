# Hospital Management System

A production-ready RESTful backend built with **Java 21** and **Spring Boot 3**, designed to manage core hospital operations including patients, doctors, appointments, departments, and insurance.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.8 |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Boilerplate | Lombok |
| Build Tool | Maven |
| Testing | Spring Boot Test (JUnit 5) |

---

## Architecture Overview

The application follows a standard **layered architecture** with a clean separation of concerns across the Controller, Service, and Repository layers. Input and output are decoupled from JPA entities using dedicated DTOs, and all exceptions are handled centrally via `@RestControllerAdvice`.
```
src/main/java/com/vyshnavi/dev/hospitalManagement/
│
├── controller/
│   ├── PatientController.java              # REST endpoints for patient CRUD
│   └── GlobalExceptionHandler.java         # Centralized exception handling
│
├── service/
│   ├── PatientService.java                 # Patient CRUD business logic
│   ├── InsuranceService.java               # Assign and remove patient insurance
│   └── AppointmentService.java             # Create and reassign appointments
│
├── repository/
│   ├── PatientRepository.java              # JPQL, native SQL, pagination, bulk update queries
│   ├── DoctorRepository.java
│   ├── AppointmentRepository.java
│   ├── DepartmentRepository.java
│   └── InsuranceRepository.java
│
├── entity/
│   ├── Patient.java
│   ├── Doctor.java
│   ├── Appointment.java
│   ├── Department.java
│   ├── Insurance.java
│   └── type/BloodGroupType.java            # Enum: A_POS, A_NEG, B_POS, B_NEG ...
│
├── dto/
│   ├── PatientRequestDto.java
│   ├── PatientResponseDto.java
│   └── BloodGroupCountDto.java             # Projection for blood group aggregation query
│
├── mapper/
│   └── PatientMapper.java                  # Manual DTO ↔ Entity conversion
│
└── exception/
    └── ResourceNotFoundException.java      # Custom 404 runtime exception
```

---

## Data Model
```
Patient       ──(1:1)──  Insurance
Patient       ──(1:N)──  Appointment  ──(N:1)──  Doctor
Department    ──(M:N)──  Doctor                  [join table: department_doctors]
Department    ──(1:1)──  Doctor                  [head doctor]
```

| Relationship | Entities | Owning Side |
|---|---|---|
| `@OneToOne` | Patient ↔ Insurance | Patient (`patient_insurance_id`) |
| `@OneToMany` / `@ManyToOne` | Patient ↔ Appointment | Appointment (`patient_id`) |
| `@OneToMany` / `@ManyToOne` | Doctor ↔ Appointment | Appointment (`doctor_id`) |
| `@ManyToMany` | Department ↔ Doctor | Department (`department_doctors`) |
| `@OneToOne` | Department → Doctor (head) | Department |

---

## API Reference

### Patient Endpoints — `/api/patients`

| Method | Endpoint | Description | Status |
|---|---|---|---|
| `POST` | `/api/patients` | Create a new patient | `201 Created` |
| `GET` | `/api/patients` | Retrieve all patients | `200 OK` |
| `GET` | `/api/patients/{id}` | Retrieve a patient by ID | `200 OK` |
| `PUT` | `/api/patients/{id}` | Update patient details | `200 OK` |
| `DELETE` | `/api/patients/{id}` | Delete a patient | `204 No Content` |

#### Create Patient
```http
POST /api/patients
Content-Type: application/json

{
  "name": "John Doe",
  "birthDate": "1995-06-15",
  "email": "johndoe@example.com",
  "gender": "MALE",
  "bloodGroup": "A_POS"
}
```
```http
HTTP/1.1 201 Created
Location: /api/patients/6

{
  "id": 6,
  "name": "John Doe",
  "birthDate": "1995-06-15",
  "email": "johndoe@example.com",
  "gender": "MALE",
  "bloodGroup": "A_POS"
}
```

#### Error Responses
```http
HTTP/1.1 400 Bad Request

{
  "error": "Validation Failed",
  "fieldErrors": {
    "name": "name must not be blank",
    "email": "must be a well-formed email address",
    "birthDate": "birthDate must be in the past",
    "bloodGroup": "bloodGroup is required"
  }
}
```
```http
HTTP/1.1 404 Not Found

{
  "error": "Not Found",
  "message": "Patient not found with id: 99"
}
```

---

## Key Design Decisions

**DTO Pattern** — API inputs and outputs are represented by dedicated request/response DTOs, keeping the persistence layer fully decoupled from the API contract.

**Input Validation** — All incoming data is validated using Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Past`, `@NotNull`). Validation errors return structured field-level messages.

**N+1 Prevention** — Associations that risk N+1 query problems are addressed using `LEFT JOIN FETCH`, loading related entities in a single database round-trip.

**Transaction Management** — Read operations are annotated with `@Transactional(readOnly = true)` to enable Hibernate optimizations and reduce unnecessary flush checks.

**Enum Storage** — Blood group is persisted as `EnumType.STRING` to ensure data remains readable and migration-safe.

**Audit Fields** — `@CreationTimestamp` with `updatable = false` is applied to `Patient` and `Insurance` to create an immutable created-at record.

**Unique Constraints** — A composite constraint on `(name, birth_date)` prevents duplicate patient records. Unique email constraints are enforced on `Patient`, `Doctor`, and `Insurance`.

**Security** — The database password is externalized to the `${DB_PASSWORD}` environment variable and is never hardcoded in configuration files.

---

## Custom Repository Queries

The `PatientRepository` demonstrates eight distinct query patterns supported by Spring Data JPA:
```java
// 1. Derived method query — by name
Patient findByName(String name);

// 2. Derived method query — by birth date or email
List<Patient> findByBirthDateOrEmail(LocalDate birthDate, String email);

// 3. JPQL — filter by blood group
@Query("SELECT p FROM Patient p WHERE p.bloodGroup = ?1")
List<Patient> findByBloodGroup(BloodGroupType bloodGroup);

// 4. JPQL — patients born after a given date
@Query("SELECT p FROM Patient p WHERE p.birthDate > :birthDate")
List<Patient> findByBornAfterDate(@Param("birthDate") LocalDate birthDate);

// 5. DTO Projection — blood group distribution
@Query("SELECT new com.vyshnavi.dev.hospitalManagement.dto.BloodGroupCountDto(p.bloodGroup, COUNT(p)) " +
       "FROM Patient p GROUP BY p.bloodGroup")
List<BloodGroupCountDto> countEachBloodGroupType();

// 6. Native SQL — paginated patient list
@Query(value = "SELECT * FROM patient", nativeQuery = true)
Page<Patient> findAllPatients(Pageable pageable);

// 7. Bulk update
@Transactional
@Modifying
@Query("UPDATE Patient p SET p.name = :name WHERE p.id = :id")
int updateNameById(@Param("name") String name, @Param("id") Long id);

// 8. Fetch join — resolves N+1 for appointments
@Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments")
List<Patient> findAllPatientsWithAppointments();
```

---
## Getting Started

### Prerequisites

- Java 21+
- PostgreSQL
- Maven
- IntelliJ IDEA (recommended)

### 1. Clone the repository
```bash
git clone https://github.com/your-username/hospitalManagement.git
cd hospitalManagement
```

### 2. Create the database
```sql
CREATE DATABASE hospitalDB;
```

### 3. Configure the environment variable

**Option A — IntelliJ IDEA (recommended)**

1. Go to **Run** → **Edit Configurations**
2. Select your Spring Boot run configuration
3. Click the browse icon next to **Environment variables**
4. Click **+** and add:
   - Name: `DB_PASSWORD`
   - Value: `your_postgres_password`
5. Click **OK** → **Apply** → **OK**

**Option B — Terminal**
```bash
# macOS / Linux
export DB_PASSWORD=your_postgres_password

# Windows
set DB_PASSWORD=your_postgres_password
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`. Sample data is loaded automatically via `data.sql`.

> **Note:** The default `ddl-auto` is set to `create`, which drops and recreates the schema on every startup. Switch to `update` or `validate` before deploying to staging or production.
---

## Seed Data

On startup, `data.sql` populates the database with:

- 5 patients across various blood groups and demographics
- 3 doctors across Cardiology, Neurology, and Dermatology
- 6 appointments linking patients to their respective doctors

---

## Testing

Integration tests are written with `@SpringBootTest` to exercise the full application stack.
```bash
./mvnw test
```

| Test Class | Scope |
|---|---|
| `PatientTests` | Fetch join query, `getPatientById`, paginated native query with sorting |
| `InsuranceTests` | Assign insurance, remove insurance, create and reassign appointments |
| `HospitalManagementApplicationTests` | Application context loads successfully |

---

## Roadmap

- [ ] Spring Security with JWT authentication and role-based access control (`ADMIN`, `DOCTOR`, `PATIENT`)
- [ ] REST controllers for Doctor, Appointment, and Department resources
- [ ] Database schema versioning with Flyway
- [ ] Unit tests for the service layer using Mockito
- [ ] Automated DTO mapping with MapStruct

---

## Author

**Kotha Sree Vyshnavi**  
[LinkedIn](https://www.linkedin.com/in/kotha-sree-vyshnavi-438736277/)
