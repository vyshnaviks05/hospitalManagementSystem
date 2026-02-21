package com.vyshnavi.dev.hospitalManagement.service;

import com.vyshnavi.dev.hospitalManagement.entity.*;
import com.vyshnavi.dev.hospitalManagement.repository.InsuranceRepository;
import com.vyshnavi.dev.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    private final PatientRepository patientRepository;

    @Transactional
    public Patient assignInsuranceToPatient(Insurance insurance,Long patientId){
        Patient patient=patientRepository.findById(patientId).orElseThrow(()-> new EntityNotFoundException("Patient not found with id: "+ patientId));
        patient.setInsurance(insurance);

        insurance.setPatient(patient); //bidirectional consistency maintainance

        return patient;
    }

    @Transactional
    public Patient disassociateInsuranceToPatient(Long patientId){
        Patient patient=patientRepository.findById(patientId).orElseThrow(()-> new EntityNotFoundException("Patient not found with id: "+ patientId));

        patient.setInsurance(null);
        return patient;
    }
    
}
