package com.vyshnavi.dev.hospitalManagement.repository;

import com.vyshnavi.dev.hospitalManagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentTime(
            Long doctorId,
            LocalDateTime appointmentTime
    );
}