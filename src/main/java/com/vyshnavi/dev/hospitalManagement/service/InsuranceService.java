package com.vyshnavi.dev.hospitalManagement.service;

import com.vyshnavi.dev.hospitalManagement.entity.Insurance;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.exception.ResourceNotFoundException;
import com.vyshnavi.dev.hospitalManagement.repository.InsuranceRepository;
import com.vyshnavi.dev.hospitalManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final PatientRepository patientRepository;

    /**
     * Assigns the given insurance policy to the patient identified by patientId.
     * Maintains bidirectional consistency between Patient and Insurance.
     */
    @Transactional
    public Patient assignInsuranceToPatient(Insurance insurance, Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // Maintain bidirectional consistency
        insurance.setPatient(patient);
        patient.setInsurance(insurance);

        return patient;
    }

    /**
     * Removes and deletes the insurance associated with the given patient.
     * The insurance record is deleted via orphanRemoval on the Patient side.
     */
    @Transactional
    public Patient removeInsuranceFromPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        patient.setInsurance(null);

        return patient;
    }
}
