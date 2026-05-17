-- ===============================
-- PATIENTS
-- ===============================

INSERT INTO patient (name, gender, birth_date, email, blood_group, created_at, updated_at)
VALUES
('Vyshu', 'FEMALE', '2003-04-08', 'vyshuk@gmail.com', 'O_POS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chikki', 'FEMALE', '2006-06-23', 'chikkik@gmail.com', 'O_POS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Amrutha', 'FEMALE', '2002-10-02', 'amrutha@gmail.com', 'AB_POS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Raj', 'MALE', '2001-04-29', 'rajj@gmail.com', 'B_NEG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ram', 'MALE', '1999-02-07', 'ramss@gmail.com', 'AB_POS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- DOCTORS
-- ===============================

INSERT INTO doctor (name, specialization, email, created_at, updated_at)
VALUES
('Dr.Vyshnavi', 'Cardiology', 'vyshnavidr@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr.Janani', 'Neurology', 'jananidr@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr.Shruthi', 'Dermatology', 'shruthidr@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- APPOINTMENTS
-- ===============================

INSERT INTO appointment (appointment_time, reason, status, doctor_id, patient_id, created_at, updated_at)
VALUES
('2026-12-05 10:00:00', 'General checkup', 'SCHEDULED', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2026-12-06 12:25:00', 'Consultation', 'COMPLETED', 2, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2026-12-07 11:00:00', 'Headache', 'SCHEDULED', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2026-12-08 11:20:00', 'Skin rash', 'CANCELLED', 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2026-12-09 10:45:00', 'Follow up visit', 'SCHEDULED', 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2026-12-10 12:00:00', 'Allergy', 'SCHEDULED', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- INSURANCE
-- ===============================

INSERT INTO insurance (policy_number, provider, valid_until, created_at, updated_at)
VALUES
('POL1001', 'Star Health', '2027-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('POL1002', 'HDFC Ergo', '2028-06-30', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- LINK INSURANCE TO PATIENT
-- ===============================

UPDATE patient SET insurance_id = 1 WHERE id = 1;
UPDATE patient SET insurance_id = 2 WHERE id = 2;

-- ===============================
-- Department
-- ===============================

INSERT INTO department (name, head_doctor_id)
VALUES
('Cardiology', 1),
('Neurology', 2);

-- ===============================
-- Link Department to Doctor
-- ===============================

INSERT INTO department_doctors (department_id, doctor_id)
VALUES
(1,1),
(2,2),
(1,3);

-- ===============================
-- USERS
-- password = password123
-- ===============================

INSERT INTO app_user (username, password, role)
VALUES
('admin', '$2a$12$Rc82UpsFT0.Ym576yUgDq.DbWnw.Ay3GZu.aiyn8c.2hYKXwDa3qm', 'ADMIN'),
('doctoruser', '$2a$12$L4d3vyZBi9gd5nDsEyM67O.zBTKI.mD0PKkJHuFqoz4K6RXb5hIzG', 'DOCTOR'),
('patientuser', '$2a$12$wfaXJrKICM1sczYwuCkzK.rTbh5ufIybekCL1/xPbxaBiJLb2ldUi', 'PATIENT');