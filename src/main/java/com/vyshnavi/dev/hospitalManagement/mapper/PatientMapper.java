package com.vyshnavi.dev.hospitalManagement.mapper;

import com.vyshnavi.dev.hospitalManagement.dto.PatientRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.PatientResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDto requestDto) {

        if (requestDto == null) {
            return null;
        }

        Patient patient = new Patient();

        patient.setName(requestDto.getName());
        patient.setBirthDate(requestDto.getBirthDate());
        patient.setEmail(requestDto.getEmail());
        patient.setGender(requestDto.getGender());
        patient.setBloodGroup(requestDto.getBloodGroup());

        return patient;
    }

    public PatientResponseDto toResponseDto(Patient patient) {

        if (patient == null) {
            return null;
        }

        return new PatientResponseDto(
                patient.getId(),
                patient.getName(),
                patient.getBirthDate(),
                patient.getEmail(),
                patient.getGender(),
                patient.getBloodGroup()
        );
    }
}