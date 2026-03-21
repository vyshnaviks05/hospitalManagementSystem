# 🏥 Hospital Management System

A RESTful backend application built with **Java 21** and **Spring Boot 3**, designed to manage core hospital operations including patients, doctors, appointments, departments, and insurance.

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

## 🏗 Architecture Overview

The application follows a standard layered architecture with a clean separation of concerns across the **Controller**, **Service**, and **Repository** layers. Input and output are decoupled from JPA entities using dedicated DTOs with manual mapper classes, and all exceptions are handled centrally via `@RestControllerAdvice`.

```
src/main/java/com/vyshnavi/dev/hospitalManagement/
│
├── controller/
│   ├── PatientController.java              # REST endpoints for patient CRUD
│   ├── DoctorController.java               # REST endpoints for doctor CRUD
│   ├── AppointmentController.java          # Create appointment and reassign to another doctor
│   ├── InsuranceController.java            # Assign and remove patient insurance
│   └── GlobalExceptionHandler.java         # Centralized exception handling (@RestControllerAdvice)
│
├── service/
│   ├── PatientService.java                 # Patient CRUD business logic
│   ├── DoctorService.java                  # Doctor CRUD business logic
│   ├── InsuranceService.java               # Assign and remove patient insurance
│   └── AppointmentService.java             # Create appointments and reassign to another doctor
│
├── repository/
│   ├── PatientRepository.java              # JPQL, native SQL, pagination, bulk update, fetch join queries
│   ├── DoctorRepository.java
│   ├── AppointmentRepository.java
│   ├── DepartmentRepository.java
│   └── InsuranceRepository.java
│
├── entity/
│   ├── Patient.java                        # Core entity with constraints and JPA relationships
│   ├── Doctor.java                         # Specialization, departments, appointments
│   ├── Appointment.java                    # Links Patient ↔ Doctor with time and reason
│   ├── Department.java                     # Has a head doctor and many doctors (ManyToMany)
│   ├── Insurance.java                      # OneToOne with Patient (policy, provider, validity)
│   └── type/BloodGroupType.java            # Enum: A_POS, A_NEG, B_POS, B_NEG, AB_POS, AB_NEG, O_POS, O_NEG
│
├── dto/
│   ├── PatientRequestDto.java              # Input DTO with Bean Validation annotations
│   ├── PatientResponseDto.java             # Output DTO for patient responses
│   ├── DoctorRequestDto.java               # Input DTO for doctor creation and update
│   ├── DoctorResponseDto.java              # Output DTO for doctor responses
│   ├── AppointmentRequestDto.java          # Input DTO with doctorId, patientId, time, reason
│   ├── AppointmentResponseDto.java         # Output DTO with patient and doctor details
│   ├── InsuranceRequestDto.java            # Input DTO for insurance assignment
│   ├── InsuranceResponseDto.java           # Output DTO with insurance and patient details
│   └── BloodGroupCountDto.java             # Projection DTO for blood group aggregation query
│
├── mapper/
│   ├── PatientMapper.java                  # Manual DTO ↔ Entity conversion for Patient
│   ├── DoctorMapper.java                   # Manual DTO ↔ Entity conversion for Doctor
│   ├── AppointmentMapper.java              # Entity → ResponseDto conversion for Appointment
│   └── InsuranceMapper.java                # Manual DTO ↔ Entity conversion for Insurance
│
└── exception/
    └── ResourceNotFoundException.java      # Custom 404 runtime exception
```

---

## 🔗 Entity Relationships

```
Patient       ──(1:1)──  Insurance
Patient       ──(1:N)──  Appointment  ──(N:1)──  Doctor
Department    ──(M:N)──  Doctor                  [join table: department_doctors]
Department    ──(1:1)──  Doctor                  [head doctor]
```

| Relationship | Entities | Owning Side |
|---|---|---|
| @OneToOne | Patient ↔ Insurance | Patient (`patient_insurance_id`) |
| @OneToMany / @ManyToOne | Patient ↔ Appointment | Appointment (`patient_id`) |
| @OneToMany / @ManyToOne | Doctor ↔ Appointment | Appointment (`doctor_id`) |
| @ManyToMany | Department ↔ Doctor | Department (`department_doctors`) |
| @OneToOne | Department → Doctor (head) | Department |

---

## 🚀 REST API Endpoints

### Patient — `/api/patients`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/api/patients` | Create a new patient | 201 Created |
| GET | `/api/patients` | Retrieve all patients | 200 OK |
| GET | `/api/patients/{id}` | Retrieve a patient by ID | 200 OK |
| PUT | `/api/patients/{id}` | Update patient details | 200 OK |
| DELETE | `/api/patients/{id}` | Delete a patient | 204 No Content |

### Doctor — `/api/doctors`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/api/doctors` | Create a new doctor | 201 Created |
| GET | `/api/doctors` | Retrieve all doctors | 200 OK |
| GET | `/api/doctors/{id}` | Retrieve a doctor by ID | 200 OK |
| PUT | `/api/doctors/{id}` | Update doctor details | 200 OK |
| DELETE | `/api/doctors/{id}` | Delete a doctor | 204 No Content |

### Appointment — `/api/appointments`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/api/appointments` | Create a new appointment | 201 Created |
| PATCH | `/api/appointments/{id}/reassign?doctorId={id}` | Reassign appointment to another doctor | 200 OK |

### Insurance — `/api/patients/{patientId}/insurance`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/api/patients/{patientId}/insurance` | Assign insurance to a patient | 200 OK |
| DELETE | `/api/patients/{patientId}/insurance` | Remove insurance from a patient | 204 No Content |

---

## 📋 Sample Requests and Responses

### Create Patient
```
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
```
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

### Create Doctor
```
POST /api/doctors
Content-Type: application/json

{
  "name": "Dr. Priya Sharma",
  "specialization": "Cardiology",
  "email": "priya.sharma@hospital.com"
}
```
```
HTTP/1.1 201 Created
Location: /api/doctors/4

{
  "id": 4,
  "name": "Dr. Priya Sharma",
  "specialization": "Cardiology",
  "email": "priya.sharma@hospital.com"
}
```

### Create Appointment
```
POST /api/appointments
Content-Type: application/json

{
  "appointmentTime": "2026-06-15T10:30:00",
  "reason": "Routine Checkup",
  "doctorId": 2,
  "patientId": 1
}
```
```
HTTP/1.1 201 Created
Location: /api/appointments/7

{
  "id": 7,
  "appointmentTime": "2026-06-15T10:30:00",
  "reason": "Routine Checkup",
  "patientId": 1,
  "patientName": "Ram",
  "doctorId": 2,
  "doctorName": "Dr. Anil Kumar"
}
```

### Reassign Appointment
```
PATCH /api/appointments/7/reassign?doctorId=3
```
```
HTTP/1.1 200 OK

{
  "id": 7,
  "appointmentTime": "2026-06-15T10:30:00",
  "reason": "Routine Checkup",
  "patientId": 1,
  "patientName": "Ram",
  "doctorId": 3,
  "doctorName": "Dr. Meena Rao"
}
```

### Assign Insurance
```
POST /api/patients/1/insurance
Content-Type: application/json

{
  "policyNumber": "HDFC1234",
  "provider": "HDFC Ergo",
  "validUntil": "2028-09-01"
}
```
```
HTTP/1.1 200 OK

{
  "id": 1,
  "policyNumber": "HDFC1234",
  "provider": "HDFC Ergo",
  "validUntil": "2028-09-01",
  "createdAt": "2026-03-21T10:00:00",
  "patientId": 1,
  "patientName": "Ram"
}
```

### Error Responses

**400 Bad Request — Validation failure**
```json
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

**404 Not Found**
```json
{
  "error": "Not Found",
  "message": "Patient not found with id: 99"
}
```

---

## ✨ Key Design Decisions

**Layered Architecture** — Controller handles HTTP, Service handles business logic and transactions, Repository handles data access. Each layer has exactly one responsibility.

**DTO Pattern** — Dedicated request and response DTOs for every entity decouple the API contract from JPA entities. Manual mapper classes handle conversion following the Single Responsibility Principle.

**Input Validation** — Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Past`, `@Future`, `@NotNull`) on all request DTOs. Validation failures return structured field-level error messages via `GlobalExceptionHandler`.

**Centralized Exception Handling** — `@RestControllerAdvice` handles `ResourceNotFoundException` (404) and `MethodArgumentNotValidException` (400) globally, returning consistent JSON error responses across all endpoints.

**N+1 Prevention** — `LEFT JOIN FETCH` used in `findAllPatientsWithAppointments()` to load patients and their appointments in a single SQL query instead of one query per patient.

**Transaction Management** — `@Transactional(readOnly = true)` on all read operations disables Hibernate dirty checking for performance optimization. Write operations use `@Transactional` for automatic rollback on failure.

**Nested Resource URL for Insurance** — Insurance endpoints use `/api/patients/{patientId}/insurance` because insurance only exists in the context of a patient — expressing resource ownership clearly in the URL.

**PATCH for Partial Update** — Appointment reassignment uses `PATCH /api/appointments/{id}/reassign?doctorId=` instead of PUT, since only one field (the assigned doctor) is being changed.

**Enum Storage** — Blood group stored as `EnumType.STRING` for readability and migration safety.

**Audit Fields** — `@CreationTimestamp` with `updatable = false` on both Patient and Insurance creates an immutable created-at timestamp.

**Security** — Database password is externalized to the `${DB_PASSWORD}` environment variable and never hardcoded in configuration files.

---

## 🗄 Custom Repository Queries

The `PatientRepository` demonstrates 8 distinct query patterns supported by Spring Data JPA:

```java
// 1. Derived method — by name
Patient findByName(String name);

// 2. Derived method — by birth date OR email
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

// 6. Native SQL with pagination
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

## ⚙️ Getting Started

### Prerequisites
- Java 21+
- PostgreSQL
- Maven
- IntelliJ IDEA (recommended)

### 1. Clone the repository
```bash
git clone https://github.com/vyshnaviks05/hospitalManagementSystem.git
cd hospitalManagementSystem
```

### 2. Create the database
```sql
CREATE DATABASE hospitalDB;
```

### 3. Set the environment variable

The database password is read from an environment variable — never hardcoded.

**Option A — IntelliJ IDEA (recommended)**
1. Go to **Run → Edit Configurations**
2. Select your Spring Boot run configuration
3. Click the browse icon next to **Environment variables**
4. Click **+** and add: `DB_PASSWORD = your_postgres_password`
5. Click **OK → Apply → OK**

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

> ⚠️ `spring.jpa.hibernate.ddl-auto=create` drops and recreates the schema on every startup. Switch to `update` or `validate` before deploying to staging or production.

---

## 🌱 Seed Data

On startup, `data.sql` populates the database with:
- 5 patients across various blood groups and demographics
- 3 doctors across Cardiology, Neurology, and Dermatology
- 6 appointments linking patients to their respective doctors

---

## 🩸 Blood Group Types

`A_POS` · `A_NEG` · `B_POS` · `B_NEG` · `AB_POS` · `AB_NEG` · `O_POS` · `O_NEG`

---

## 🧪 Testing

Integration tests use `@SpringBootTest` to exercise the full application stack against a real PostgreSQL database.

```bash
./mvnw test
```

| Test Class | Coverage |
|---|---|
| `PatientTests` | Fetch join query, getPatientById, paginated native query with sorting, ResourceNotFoundException for missing patient |
| `InsuranceTests` | Assign insurance, remove insurance, create appointment, reassign appointment to another doctor |
| `HospitalManagementApplicationTests` | Application context loads successfully |

---

## 🔮 Roadmap

- [ ] Spring Security with JWT authentication and role-based access control (ADMIN, DOCTOR, PATIENT)
- [ ] Unit tests for the service layer using Mockito
- [ ] Automated DTO mapping with MapStruct

---

## 👩‍💻 Author

**Kotha Sree Vyshnavi**
- 💼 [LinkedIn](https://www.linkedin.com/in/kotha-sree-vyshnavi-438736277/)
- 🐙 [GitHub](https://github.com/vyshnaviks05)
- 📧 vyshukotha05@gmail.com
