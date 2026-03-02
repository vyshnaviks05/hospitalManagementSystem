package com.vyshnavi.dev.hospitalManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {

    private Long id;
    private LocalDateTime appointmentTime;
    private String reason;

    // Only expose IDs to avoid exposing full nested objects
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
}
