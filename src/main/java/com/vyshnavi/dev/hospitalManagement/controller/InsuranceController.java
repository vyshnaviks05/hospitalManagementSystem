package com.vyshnavi.dev.hospitalManagement.controller;

import com.vyshnavi.dev.hospitalManagement.dto.InsuranceRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.InsuranceResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Insurance;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.mapper.InsuranceMapper;
import com.vyshnavi.dev.hospitalManagement.service.InsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/{patientId}/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;
    private final InsuranceMapper insuranceMapper;

    // POST /api/patients/{patientId}/insurance
    // Assigns an insurance policy to the given patient.
    // Uses nested URL because insurance only makes sense in context of a patient.
    // Returns 200 OK with the insurance details.
    @PostMapping
    public ResponseEntity<InsuranceResponseDto> assignInsurance(
            @PathVariable Long patientId,
            @Valid @RequestBody InsuranceRequestDto dto) {

        Insurance insurance = insuranceMapper.toEntity(dto);
        Patient patient = insuranceService.assignInsuranceToPatient(insurance, patientId);

        return ResponseEntity.ok(insuranceMapper.toResponseDto(patient));
    }

    // DELETE /api/patients/{patientId}/insurance
    // Removes and deletes the insurance from the given patient.
    // orphanRemoval=true on Patient handles the actual DB delete.
    // Returns 204 No Content.
    @DeleteMapping
    public ResponseEntity<Void> removeInsurance(@PathVariable Long patientId) {
        insuranceService.removeInsuranceFromPatient(patientId);
        return ResponseEntity.noContent().build();
    }
}
