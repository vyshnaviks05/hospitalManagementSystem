package com.vyshnavi.dev.hospitalManagement.service;

import com.vyshnavi.dev.hospitalManagement.entity.Appointment;
import com.vyshnavi.dev.hospitalManagement.entity.Doctor;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.entity.type.AppointmentStatus;
import com.vyshnavi.dev.hospitalManagement.exception.AppointmentConflictException;
import com.vyshnavi.dev.hospitalManagement.exception.ResourceNotFoundException;
import com.vyshnavi.dev.hospitalManagement.repository.AppointmentRepository;
import com.vyshnavi.dev.hospitalManagement.repository.DoctorRepository;
import com.vyshnavi.dev.hospitalManagement.repository.PatientRepository;
import com.vyshnavi.dev.hospitalManagement.dto.AppointmentResponseDto;
import com.vyshnavi.dev.hospitalManagement.mapper.AppointmentMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public Appointment createNewAppointment(Appointment appointment, Long doctorId, Long patientId) {
        if (appointment.getId() != null) {
            throw new IllegalArgumentException("New appointment must not have an id");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        boolean appointmentExists =
                appointmentRepository.existsByDoctorIdAndAppointmentTime(
                        doctorId,
                        appointment.getAppointmentTime()
                );

        if (appointmentExists) {
            throw new AppointmentConflictException(
                    "Doctor already has an appointment at this time"
            );
        }

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment reassignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        validateAppointmentIsActive(appointment);

        // Dirty checking will trigger the UPDATE automatically
        appointment.setDoctor(doctor);

        return appointment;
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Appointment not found with id: " + id));
        return appointmentMapper.toResponseDto(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAllAppointments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentTime").ascending());
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toResponseDto);
    }

    @Transactional
    public Appointment cancelAppointment(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id: " + appointmentId
                        ));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment is already cancelled");
        }

        log.info(
                "Cancelling appointment with id: {}",
                appointmentId
        );

        appointment.setStatus(AppointmentStatus.CANCELLED);

        return appointment;
    }

    private void validateAppointmentIsActive(Appointment appointment) {

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled appointments cannot be modified"
            );
        }
    }

    @Transactional
    public Appointment completeAppointment(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id: " + appointmentId
                        ));

        validateAppointmentIsActive(appointment);

        appointment.setStatus(AppointmentStatus.COMPLETED);

        return appointment;
    }
}
