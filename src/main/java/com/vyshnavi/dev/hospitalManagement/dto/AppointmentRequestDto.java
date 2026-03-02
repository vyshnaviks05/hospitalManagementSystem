package com.vyshnavi.dev.hospitalManagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDto {

    @NotNull(message = "appointmentTime is required")
    @Future(message = "appointmentTime must be in the future")
    private LocalDateTime appointmentTime;

    private String reason;

    @NotNull(message = "doctorId is required")
    private Long doctorId;

    @NotNull(message = "patientId is required")
    private Long patientId;
}
