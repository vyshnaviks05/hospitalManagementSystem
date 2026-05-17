package com.vyshnavi.dev.hospitalManagement.service;

import com.vyshnavi.dev.hospitalManagement.dto.PatientRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.PatientResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.exception.ResourceNotFoundException;
import com.vyshnavi.dev.hospitalManagement.exception.DuplicateResourceException;
import com.vyshnavi.dev.hospitalManagement.mapper.PatientMapper;
import com.vyshnavi.dev.hospitalManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long id) {
        Patient patient = findPatientById(id);
        return patientMapper.toResponseDto(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDto> getAllPatients(
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponseDto);
    }

    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto requestDto) {

        Patient patient = patientMapper.toEntity(requestDto);

        if (patientRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException(
                    "Patient already exists with email: " + requestDto.getEmail()
            );
        }

        log.info(
                "Creating patient with email: {}",
                requestDto.getEmail()
        );

        Patient savedPatient = patientRepository.save(patient);

        log.info(
                "Patient created successfully with id: {}",
                savedPatient.getId()
        );

        return patientMapper.toResponseDto(savedPatient);
    }

    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientRequestDto requestDto) {

        Patient existingPatient = findPatientById(id);

        if (!existingPatient.getEmail().equals(requestDto.getEmail())
                && patientRepository.existsByEmail(requestDto.getEmail())) {

            throw new DuplicateResourceException(
                    "Patient already exists with email: " + requestDto.getEmail()
            );
        }

        existingPatient.setName(requestDto.getName());
        existingPatient.setBirthDate(requestDto.getBirthDate());
        existingPatient.setEmail(requestDto.getEmail());
        existingPatient.setGender(requestDto.getGender());
        existingPatient.setBloodGroup(requestDto.getBloodGroup());

        return patientMapper.toResponseDto(existingPatient);
    }

    @Transactional
    public void deletePatient(Long id) {

        Patient patient = findPatientById(id);

        patientRepository.delete(patient);
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDto> searchPatientsByName(
            String name,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return patientRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(patientMapper::toResponseDto);
    }
}