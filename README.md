# 🏥 Hospital Management System

A **RESTful backend application** built with **Java, Spring Boot, and PostgreSQL** that manages core hospital operations — patients, doctors, appointments, departments, and insurance.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
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
│   ├── PatientController.java          # REST endpoints for patient operations
│   └── GlobalExceptionHandler.java     # Centralized exception handling
│
├── service/
│   ├── PatientService.java             # Patient business logic
│   ├── InsuranceService.java           # Insurance assign/disassociate logic
│   └── AppointmentService.java         # Appointment create/reassign logic
│
├── repository/
│   ├── PatientRepository.java          # Custom JPQL, native SQL, pagination queries
│   ├── DoctorRepository.java
│   ├── AppointmentRepository.java
│   ├── DepartmentRepository.java
│   └── InsuranceRepository.java
│
├── entity/
│   ├── Patient.java                    # Core entity with constraints & relationships
│   ├── Doctor.java                     # Specialization, departments, appointments
│   ├── Appointment.java                # Links Patient ↔ Doctor
│   ├── Department.java                 # Has head doctor + many doctors
│   ├── Insurance.java                  # OneToOne with Patient
│   └── type/BloodGroupType.java        # Enum: A_POS, B_NEG, AB_POS, O_NEG...
│
├── dto/
│   ├── PatientRequestDto.java          # Input DTO with Bean Validation
│   ├── PatientResponseDto.java         # Output DTO
│   └── BloodGroupCountResponseEntity.java  # DTO projection for grouped query
│
├── mapper/
│   └── PatientMapper.java              # Manual DTO ↔ Entity conversion
│
└── exception/
    └── ResourceNotFoundException.java  # Custom runtime exception
```

---

## 🔗 Entity Relationships

```
Patient  ──(1:1)──  Insurance
Patient  ──(1:N)──  Appointment  ──(N:1)──  Doctor
Department  ──(M:N)──  Doctor        [join table: my_dpt_doctors]
Department  ──(1:1)──  Doctor        [head doctor]
```

| Relationship | Entities | Owner Side |
|---|---|---|
| OneToOne | Patient ↔ Insurance | Patient (`@JoinColumn`) |
| OneToMany / ManyToOne | Patient ↔ Appointment | Appointment (`@JoinColumn`) |
| ManyToMany | Department ↔ Doctor | Department (`@JoinTable`) |
| OneToOne | Department → Doctor (head) | Department |

---

## 🚀 REST API Endpoints

### Patient

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `POST` | `/api/patients` | Create a new patient | `201 Created` |
| `GET` | `/api/patients` | Get all patients | `200 OK` |
| `GET` | `/api/patients/{id}` | Get patient by ID | `200 OK` |
| `PUT` | `/api/patients/{id}` | Update patient | `200 OK` |
| `DELETE` | `/api/patients/{id}` | Delete patient | `204 No Content` |

### Sample Request — Create Patient

```json
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

```json
HTTP/1.1 201 Created
Location: /api/patients/1

{
  "id": 1,
  "name": "John Doe",
  "birthDate": "1995-06-15",
  "email": "johndoe@example.com",
  "gender": "MALE",
  "bloodGroup": "A_POS"
}
```

### Validation Error Response

```json
HTTP/1.1 400 Bad Request

{
  "error": "Validation Failed",
  "fieldErrors": {
    "name": "name must not be blank",
    "email": "must be a well-formed email address",
    "birthDate": "birthDate must be in the past"
  }
}
```

---

## ✨ Key Features

- **Layered Architecture** — Controller → Service → Repository with clean separation of concerns
- **DTO Pattern** — Request and Response DTOs decouple the API contract from database entities
- **Input Validation** — Jakarta Bean Validation with field-level error messages
- **Global Exception Handling** — `@RestControllerAdvice` returns structured JSON for `404` and `400` errors
- **Custom Queries** — 7+ query types including JPQL, native SQL, DTO projections, bulk updates, fetch joins, and pagination
- **Transaction Management** — `@Transactional(readOnly=true)` on reads for performance optimization
- **JPA Relationships** — All 4 relationship types with proper cascade, `orphanRemoval`, and bidirectional consistency
- **N+1 Prevention** — `LEFT JOIN FETCH` used to load patients with appointments in a single query
- **Enum Storage** — Blood group stored as `EnumType.STRING` for data safety
- **Audit Fields** — `@CreationTimestamp` with `updatable=false` for immutable record tracking

---

## 🗄 Custom Repository Queries

```java
// 1. Derived query
Patient findByName(String name);

// 2. Custom JPQL
@Query("SELECT p FROM Patient p WHERE p.bloodGroup = ?1")
List<Patient> findByBloodGroup(BloodGroupType bloodGroup);

// 3. DTO Projection — grouped count
@Query("SELECT new com.vyshnavi.dev.hospitalManagement.dto.BloodGroupCountResponseEntity(p.bloodGroup, COUNT(p)) " +
       "FROM Patient p GROUP BY p.bloodGroup")
List<BloodGroupCountResponseEntity> countEachBloodGroupType();

// 4. Native SQL with Pagination
@Query(value = "select * from patient", nativeQuery = true)
Page<Patient> findAllPatients(Pageable pageable);

// 5. Bulk Update
@Transactional
@Modifying
@Query("UPDATE Patient p SET p.name = :name WHERE p.id = :id")
int updateNameWithId(@Param("name") String name, @Param("id") Long id);

// 6. Fetch Join — solves N+1 problem
@Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments")
List<Patient> findAllPatientsWithAppointments();
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- PostgreSQL
- Maven

### 1. Clone the repository
```bash
git clone https://github.com/your-username/hospitalManagement.git
cd hospitalManagement
```

### 2. Create PostgreSQL database
```sql
CREATE DATABASE hospitalDB;
```

### 3. Configure application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hospitalDB
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`
Sample data is automatically loaded from `data.sql` on startup.

---

## 🧪 Testing

Integration tests are written using `@SpringBootTest` to validate service and repository layer behavior end to end.

```bash
./mvnw test
```

Test coverage includes:
- Patient repository queries (fetch join, pagination, blood group filter)
- Patient service layer (`getPatientById`, etc.)
- Insurance assign and disassociate flow
- Appointment creation and doctor reassignment

---

## 📌 What I Learned

- Implementing and managing all 4 JPA relationship types with proper bidirectional consistency
- Writing advanced JPQL including DTO projections and fetch joins
- Solving the N+1 problem using `LEFT JOIN FETCH`
- Using `@Transactional(readOnly=true)` as a performance optimization
- Designing a clean REST API with proper HTTP status codes and centralized error handling
- Applying `@RestControllerAdvice` for structured, consistent error responses

---

## 🔮 Future Improvements

- [ ] Add Spring Security with JWT authentication and role-based access (ADMIN, DOCTOR, PATIENT)
- [ ] Add REST controllers for Doctor, Appointment, and Department
- [ ] Replace `ddl-auto=create` with Flyway database migrations
- [ ] Add unit tests using Mockito for service layer isolation
- [ ] Move sensitive config to environment variables
- [ ] Add MapStruct for automatic DTO mapping

---

## 👩‍💻 Author

**Kotha Sree Vyshnavi**  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat&logo=linkedin)](https://linkedin.com/in/kotha-sree-vyshnavi)
