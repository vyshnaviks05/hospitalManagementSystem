package com.vyshnavi.dev.hospitalManagement;

import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.repository.PatientRepository;
import com.vyshnavi.dev.hospitalManagement.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PatientTests {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Test
    public void testFindAllPatientsWithAppointments() {
        List<Patient> patientList = patientRepository.findAllPatientsWithAppointments();
        assertThat(patientList).isNotNull();
        System.out.println(patientList);
    }

    @Test
    public void testGetPatientById() {
        Patient patient = patientService.getPatientById(1L);
        assertThat(patient).isNotNull();
        System.out.println(patient);
    }

    @Test
    public void testFindAllPatientsPaginated() {
        Page<Patient> patientPage = patientRepository.findAllPatients(
                PageRequest.of(0, 2, Sort.by("name"))
        );
        assertThat(patientPage).isNotNull();
        patientPage.forEach(System.out::println);
    }
}
