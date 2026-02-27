# 🏥 Hospital Management System

A RESTful backend application built with **Java 21**, **Spring Boot 3**, and **PostgreSQL** that manages core hospital operations — patients, doctors, appointments, departments, and insurance.

---

## 🛠 Tech Stack

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

## 📁 Project Structure

```
src/main/java/com/vyshnavi/dev/hospitalManagement/
│
├── controller/
│   ├── PatientController.java              # REST endpoints for patient CRUD
│   └── GlobalExceptionHandler.java         # Centralized exception handling (@RestControllerAdvice)
│
├── service/
│   ├── PatientService.java                 # Patient CRUD business logic
│   ├── InsuranceService.java               # Assign / remove insurance from patient
│   └── AppointmentService.java             # Create appointment & reassign to another doctor
│
├── repository/
│   ├── PatientRepository.java              # Custom JPQL, native SQL, pagination, bulk update queries
│   ├── DoctorRepository.java
│   ├── AppointmentRepository.java
│   ├── DepartmentRepository.java
│   └── InsuranceRepository.java
│
├── entity/
│   ├── Patient.java                        # Core entity with constraints & JPA relationships
│   ├── Doctor.java                         # Specialization, departments, appointments
│   ├── Appointment.java                    # Links Patient ↔ Doctor with time and reason
│   ├── Department.java                     # Has a head doctor + many doctors (ManyToMany)
│   ├── Insurance.java                      # OneToOne with Patient (policy, provider, validity)
│   └── type/BloodGroupType.java            # Enum: A_POS, A_NEG, B_POS, B_NEG...
│
├── dto/
│   ├── PatientRequestDto.java              # Input DTO with Bean Validation annotations
│   ├── PatientResponseDto.java             # Output DTO (id, name, birthDate, email, gender, bloodGroup)
│   └── BloodGroupCountDto.java             # DTO projection for grouped blood group query
│
├── mapper/
│   └── PatientMapper.java                  # Manual DTO ↔ Entity conversion
│
└── exception/
    └── ResourceNotFoundException.java      # Custom runtime exception (404)
```

---

## 🔗 Entity Relationships

![ER Diagram](docs/er-diagram.png)

> Entity diagram generated from DBeaver showing all tables and relationships in the `hospitalDB` PostgreSQL database.

```
Patient  ──(1:1)──  Insurance
Patient  ──(1:N)──  Appointment  ──(N:1)──  Doctor
Department  ──(M:N)──  Doctor              [join table: department_doctors]
Department  ──(1:1)──  Doctor              [head doctor]
```

| Relationship | Entities | Owner Side |
|---|---|---|
| OneToOne | Patient ↔ Insurance | Patient (`@JoinColumn: patient_insurance_id`) |
| OneToMany / ManyToOne | Patient ↔ Appointment | Appointment (`@JoinColumn: patient_id`) |
| OneToMany / ManyToOne | Doctor ↔ Appointment | Appointment (`@JoinColumn: doctor_id`) |
| ManyToMany | Department ↔ Doctor | Department (`@JoinTable: department_doctors`) |
| OneToOne | Department → Doctor (head) | Department |

---

## 🚀 REST API Endpoints

### Patient — `/api/patients`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `POST` | `/api/patients` | Create a new patient | `201 Created` |
| `GET` | `/api/patients` | Get all patients | `200 OK` |
| `GET` | `/api/patients/{id}` | Get patient by ID | `200 OK` |
| `PUT` | `/api/patients/{id}` | Update patient details | `200 OK` |
| `DELETE` | `/api/patients/{id}` | Delete patient | `204 No Content` |

### Sample Request — Create Patient

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

### Sample Response

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

### Validation Error Response

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

### Not Found Response

```http
HTTP/1.1 404 Not Found

{
  "error": "Not Found",
  "message": "Patient not found with id: 99"
}
```

---

## ✨ Key Features

- **Layered Architecture** — Controller → Service → Repository with clean separation of concerns
- **DTO Pattern** — `PatientRequestDto` and `PatientResponseDto` decouple the API contract from JPA entities
- **Input Validation** — Jakarta Bean Validation (`@NotBlank`, `@Past`, `@Email`, `@NotNull`) with field-level error messages
- **Global Exception Handling** — `@RestControllerAdvice` returns structured JSON for `404` and `400` errors
- **Environment Variable Config** — Database password loaded from `${DB_PASSWORD}` env variable; never hardcoded
- **Custom Queries** — 8 query types including derived queries, JPQL, native SQL, DTO projections, bulk updates, fetch joins, and pagination
- **Transaction Management** — `@Transactional(readOnly = true)` on read operations for performance optimization
- **JPA Relationships** — All 4 relationship types with proper cascade, `orphanRemoval`, and bidirectional consistency
- **N+1 Prevention** — `LEFT JOIN FETCH` used to load patients with their appointments in a single query
- **Enum Storage** — Blood group stored as `EnumType.STRING` for readability and data safety
- **Audit Fields** — `@CreationTimestamp` with `updatable = false` on both `Patient` and `Insurance` for immutable record tracking
- **Unique Constraints** — Composite unique constraint on `(name, birth_date)` in `Patient`; unique email enforced on `Patient`, `Doctor`, and `Insurance`

---

## 🗄 Custom Repository Queries

```java
// 1. Derived query — by name
Patient findByName(String name);

// 2. Derived query — by birth date OR email
List<Patient> findByBirthDateOrEmail(LocalDate birthDate, String email);

// 3. Custom JPQL — filter by blood group
@Query("SELECT p FROM Patient p WHERE p.bloodGroup = ?1")
List<Patient> findByBloodGroup(BloodGroupType bloodGroup);

// 4. Custom JPQL — born after a given date
@Query("SELECT p FROM Patient p WHERE p.birthDate > :birthDate")
List<Patient> findByBornAfterDate(@Param("birthDate") LocalDate birthDate);

// 5. DTO Projection — grouped blood group count
@Query("SELECT new com.vyshnavi.dev.hospitalManagement.dto.BloodGroupCountDto(p.bloodGroup, COUNT(p)) " +
       "FROM Patient p GROUP BY p.bloodGroup")
List<BloodGroupCountDto> countEachBloodGroupType();

// 6. Native SQL with Pagination
@Query(value = "SELECT * FROM patient", nativeQuery = true)
Page<Patient> findAllPatients(Pageable pageable);

// 7. Bulk Update
@Transactional
@Modifying
@Query("UPDATE Patient p SET p.name = :name WHERE p.id = :id")
int updateNameById(@Param("name") String name, @Param("id") Long id);

// 8. Fetch Join — solves N+1 problem
@Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments")
List<Patient> findAllPatientsWithAppointments();
```

---

## ⚙️ Setup & Run

### Prerequisites

- Java 21+
- PostgreSQL
- Maven

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/hospitalManagement.git
cd hospitalManagement
```

### 2. Create PostgreSQL Database

```sql
CREATE DATABASE hospitalDB;
```

### 3. Set Environment Variable

The database password is read from an environment variable — never hardcoded.

```bash
# Linux / macOS
export DB_PASSWORD=your_postgres_password

# Windows (Command Prompt)
set DB_PASSWORD=your_postgres_password
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. Sample data is automatically loaded from `data.sql` on startup.

> ⚠️ `spring.jpa.hibernate.ddl-auto=create` drops and recreates the schema on every startup. Change to `update` or `validate` for staging/production.

---

## 🌱 Seed Data

`data.sql` pre-loads the database with:

- 5 patients (name, gender, birth date, email, blood group)
- 3 doctors — Cardiology, Neurology, Dermatology
- 6 appointments linking patients to doctors

---

## 🩸 Blood Group Types

`A_POS` · `A_NEG` · `B_POS` · `B_NEG` · `AB_POS` · `AB_NEG` · `O_POS` · `O_NEG`

---

## 🧪 Testing

Integration tests use `@SpringBootTest` to validate service and repository behavior end to end.

```bash
./mvnw test
```

| Test Class | Coverage |
|---|---|
| `PatientTests` | Fetch join query, `getPatientById`, paginated native query with sorting |
| `InsuranceTests` | Assign insurance, remove insurance, create appointment, reassign appointment to another doctor |
| `HospitalManagementApplicationTests` | Application context loads successfully |

---

## 🔮 Future Improvements

- [ ] Add Spring Security with JWT authentication and role-based access (ADMIN, DOCTOR, PATIENT)
- [ ] Add REST controllers for Doctor, Appointment, and Department
- [ ] Replace `ddl-auto=create` with Flyway database migrations
- [ ] Add unit tests using Mockito for service layer isolation
- [ ] Add MapStruct for automatic DTO mapping

---

## 👩‍💻 Author

**Kotha Sree Vyshnavi**
