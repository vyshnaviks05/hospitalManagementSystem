package com.vyshnavi.dev.hospitalManagement.mapper;

import com.vyshnavi.dev.hospitalManagement.dto.DoctorRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.DoctorResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorRequestDto dto) {
        if (dto == null) return null;
        Doctor doctor = new Doctor();
        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setEmail(dto.getEmail());
        return doctor;
    }

    public DoctorResponseDto toResponseDto(Doctor doctor) {
        if (doctor == null) return null;
        return new DoctorResponseDto(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getEmail()
        );
    }
}

