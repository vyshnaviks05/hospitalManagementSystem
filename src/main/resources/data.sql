INSERT INTO patient(name,gender,birth_date,email,blood_group)
VALUES
        ('Vyshu','FEMALE','2003-04-08','vyshuk@gmail.com','O_POS'),
        ('Chikki','FEMALE','2006-06-23','chikkik@gmail.com','O_POS'),
        ('Amrutha','FEMALE','2002-10-02','amrutha@gmail.com','AB_POS'),
        ('Raj','MALE','2001-04-29','rajj@gmail.com','B_NEG'),
        ('Ram','MALE','1999-02-07','ramss@gmail.com','AB_POS');

INSERT INTO doctor (name,specialization,email)
VALUES
        ('Dr.Vyshnavi','Cardiology','vyshnavidr@gmail.com'),
        ('Dr.Janani','Neurology','jananidr@gmail.com'),
        ('Dr.Shruthi','Dermatology','shruthidr@gmail.com');

INSERT INTO Appointment (appointment_time,reason,doctor_id,patient_id)
VALUES
        ('2025-12-05 10:00:00','General checkup',1,2),
        ('2025-12-06 12:25:00','Consultation',2,4),
        ('2025-12-07 11:00:00','Headache',2,1),
        ('2025-12-08 11:20:00','Skin rash',3,3),
        ('2025-12-09 10:45:00','Follow up visit',1,5),
        ('2025-12-10 12:00:00','Allergy',3,4);