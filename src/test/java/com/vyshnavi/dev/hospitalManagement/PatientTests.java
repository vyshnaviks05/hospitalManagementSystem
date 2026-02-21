package com.vyshnavi.dev.hospitalManagement;

import com.vyshnavi.dev.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.entity.type.BloodGroupType;
import com.vyshnavi.dev.hospitalManagement.repository.PatientRepository;
import com.vyshnavi.dev.hospitalManagement.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class PatientTests {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Test
    public void testPatientRepository(){
        //List<Patient> patientList=patientRepository.findAll();
        List<Patient> patientList=patientRepository.findAllPatientsWithAppointments();
        System.out.println(patientList);

    }

    @Test
    public void testPatientService(){
        Patient patient=patientService.getPatientById(1L);
        System.out.println(patient);
    }

    @Test
    public void testTransactionMethods(){
        //Patient patient=patientRepository.findByName("Ram");
//        List<Patient> patientList=patientRepository.findByBirthDateOrEmail(LocalDate.of(2003,4,8),"chikkik@gmail.com");
//
//        for(Patient patient : patientList){
//            System.out.println(patient);
//        }

        Page<Patient> patientList=patientRepository.findAllPatients(PageRequest.of(0,2, Sort.by("name")));
        for(Patient patient : patientList){
           System.out.println(patient);
       }

//        List<Patient> patientList=patientRepository.findByBloodGroup(BloodGroupType.AB_POS);
//        for(Patient patient : patientList){
//            System.out.println(patient);
//        }

//        List<Patient> patientList=patientRepository.findByBornAfterDate(LocalDate.of(2001,01,01));
//        for(Patient patient : patientList){
//            System.out.println(patient);
//        }

//        List<Object[]> bloodGroupList=patientRepository.countEachBloodGroupType();
//        for(Object[] objects : bloodGroupList){
//            System.out.println(objects[0]+" " +objects[1]);
//        }

//        int rowsUpdated=patientRepository.updateNameWithId("Vyshu Kotha",1L);
//        System.out.println(rowsUpdated);

//        List<BloodGroupCountResponseEntity> bloodGroupList=patientRepository.countEachBloodGroupType();
//        for(BloodGroupCountResponseEntity bloodGroupCountResponse : bloodGroupList){
//            System.out.println(bloodGroupCountResponse);
//        }
    }

}
