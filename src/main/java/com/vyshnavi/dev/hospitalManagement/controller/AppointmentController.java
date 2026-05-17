package com.vyshnavi.dev.hospitalManagement.controller;

import com.vyshnavi.dev.hospitalManagement.dto.AppointmentRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.AppointmentResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Appointment;
import com.vyshnavi.dev.hospitalManagement.mapper.AppointmentMapper;
import com.vyshnavi.dev.hospitalManagement.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDto>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                appointmentService.getAllAppointments(page, size)
        );
    }

    // POST /api/appointments
    // Creates a new appointment for a given patient and doctor.
    // doctorId and patientId come from the request body (AppointmentRequestDto).
    // Returns 201 Created with Location header.
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @Valid @RequestBody AppointmentRequestDto dto) {

        // Build an Appointment entity with only time and reason
        // AppointmentService fetches Doctor and Patient from DB using the IDs
        Appointment appointment = Appointment.builder()
                .appointmentTime(dto.getAppointmentTime())
                .reason(dto.getReason())
                .build();

        Appointment created = appointmentService.createNewAppointment(
                appointment, dto.getDoctorId(), dto.getPatientId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(appointmentMapper.toResponseDto(created));
    }

    // PATCH /api/appointments/{id}/reassign?doctorId=2
    // Reassigns an existing appointment to a different doctor.
    // Uses PATCH because we're partially updating one field (the doctor).
    // doctorId is a query parameter — it's a simple value, not a full request body.
    @PatchMapping("/{id}/reassign")
    public ResponseEntity<AppointmentResponseDto> reassignAppointment(
            @PathVariable Long id,
            @RequestParam Long doctorId) {

        Appointment updated = appointmentService.reassignAppointmentToAnotherDoctor(id, doctorId);
        return ResponseEntity.ok(appointmentMapper.toResponseDto(updated));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(
            @PathVariable Long id) {

        Appointment cancelledAppointment =
                appointmentService.cancelAppointment(id);

        return ResponseEntity.ok(
                appointmentMapper.toResponseDto(cancelledAppointment)
        );
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(
            @PathVariable Long id) {

        Appointment completedAppointment =
                appointmentService.completeAppointment(id);

        return ResponseEntity.ok(
                appointmentMapper.toResponseDto(completedAppointment)
        );
    }
}

