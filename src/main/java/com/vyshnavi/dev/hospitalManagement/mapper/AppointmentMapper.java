package com.vyshnavi.dev.hospitalManagement.mapper;

import com.vyshnavi.dev.hospitalManagement.dto.AppointmentResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    // No toEntity() here — AppointmentService builds the entity itself
    // using doctorId and patientId to fetch real DB objects.
    // A DTO → entity conversion here would be incomplete (no patient/doctor objects).

    public AppointmentResponseDto toResponseDto(Appointment appointment) {
        if (appointment == null) return null;
        return new AppointmentResponseDto(
                appointment.getId(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName()
        );
    }
}
