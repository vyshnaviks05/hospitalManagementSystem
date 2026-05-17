package com.vyshnavi.dev.hospitalManagement.service;

import com.vyshnavi.dev.hospitalManagement.dto.DoctorRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.DoctorResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Doctor;
import com.vyshnavi.dev.hospitalManagement.exception.DuplicateResourceException;
import com.vyshnavi.dev.hospitalManagement.exception.ResourceNotFoundException;
import com.vyshnavi.dev.hospitalManagement.mapper.DoctorMapper;
import com.vyshnavi.dev.hospitalManagement.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Transactional(readOnly = true)
    public DoctorResponseDto getDoctorById(Long id) {

        Doctor doctor = findDoctorById(id);

        return doctorMapper.toResponseDto(doctor);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponseDto> getAllDoctors(
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return doctorRepository.findAll(pageable)
                .map(doctorMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponseDto> searchDoctorsBySpecialization(
            String specialization,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return doctorRepository
                .findBySpecializationContainingIgnoreCase(
                        specialization,
                        pageable
                )
                .map(doctorMapper::toResponseDto);
    }

    @Transactional
    public DoctorResponseDto createDoctor(DoctorRequestDto requestDto) {

        if (doctorRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException(
                    "Doctor already exists with email: " +
                            requestDto.getEmail()
            );
        }

        log.info(
                "Creating doctor with email: {}",
                requestDto.getEmail()
        );

        Doctor doctor = doctorMapper.toEntity(requestDto);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.toResponseDto(savedDoctor);
    }

    @Transactional
    public DoctorResponseDto updateDoctor(
            Long id,
            DoctorRequestDto requestDto) {

        Doctor existingDoctor = findDoctorById(id);

        if (!existingDoctor.getEmail().equals(requestDto.getEmail())
                && doctorRepository.existsByEmail(requestDto.getEmail())) {

            throw new DuplicateResourceException(
                    "Doctor already exists with email: " +
                            requestDto.getEmail()
            );
        }

        existingDoctor.setName(requestDto.getName());
        existingDoctor.setSpecialization(
                requestDto.getSpecialization()
        );
        existingDoctor.setEmail(requestDto.getEmail());

        return doctorMapper.toResponseDto(existingDoctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {

        Doctor doctor = findDoctorById(id);

        doctorRepository.delete(doctor);
    }

    private Doctor findDoctorById(Long id) {

        return doctorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: " + id
                        ));
    }
}