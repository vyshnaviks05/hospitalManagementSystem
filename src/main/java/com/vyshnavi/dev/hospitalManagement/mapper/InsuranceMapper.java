package com.vyshnavi.dev.hospitalManagement.mapper;

import com.vyshnavi.dev.hospitalManagement.dto.InsuranceRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.InsuranceResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Insurance;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class InsuranceMapper {

    public Insurance toEntity(InsuranceRequestDto dto) {
        if (dto == null) return null;
        return Insurance.builder()
                .policyNumber(dto.getPolicyNumber())
                .provider(dto.getProvider())
                .validUntil(dto.getValidUntil())
                .build();
    }

    public InsuranceResponseDto toResponseDto(Patient patient) {
        // InsuranceService returns Patient (not Insurance directly)
        // so we map from patient.getInsurance() + patient details
        if (patient == null || patient.getInsurance() == null) return null;
        Insurance insurance = patient.getInsurance();
        return new InsuranceResponseDto(
                insurance.getId(),
                insurance.getPolicyNumber(),
                insurance.getProvider(),
                insurance.getValidUntil(),
                insurance.getCreatedAt(),
                patient.getId(),
                patient.getName()
        );
    }
}
