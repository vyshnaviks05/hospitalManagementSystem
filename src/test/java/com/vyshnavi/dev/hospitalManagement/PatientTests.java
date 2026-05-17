package com.vyshnavi.dev.hospitalManagement;

import com.vyshnavi.dev.hospitalManagement.dto.PatientResponseDto;
import com.vyshnavi.dev.hospitalManagement.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PatientTests {

    @Autowired
    private PatientService patientService;

    @Test
    void shouldReturnPaginatedPatients() {

        Page<PatientResponseDto> patients =
                patientService.getAllPatients(
                        0,
                        5,
                        "name",
                        "asc"
                );

        assertThat(patients).isNotNull();
        assertThat(patients.getContent()).isNotEmpty();
        assertThat(patients.getSize()).isEqualTo(5);
    }

    @Test
    void shouldSearchPatientsByName() {

        Page<PatientResponseDto> patients =
                patientService.searchPatientsByName(
                        "Vy",
                        0,
                        10,
                        "id",
                        "asc"
                );

        assertThat(patients.getContent())
                .allMatch(patient ->
                        patient.getName().contains("Vy"));
    }
}