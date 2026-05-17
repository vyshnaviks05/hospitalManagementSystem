package com.vyshnavi.dev.hospitalManagement;

import com.vyshnavi.dev.hospitalManagement.entity.Appointment;
import com.vyshnavi.dev.hospitalManagement.entity.Insurance;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.entity.type.AppointmentStatus;
import com.vyshnavi.dev.hospitalManagement.service.AppointmentService;
import com.vyshnavi.dev.hospitalManagement.service.InsuranceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InsuranceTests {

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private AppointmentService appointmentService;

    @Test
    void shouldAssignAndRemoveInsurance() {

        Insurance insurance = Insurance.builder()
                .policyNumber("TEST-POLICY-101")
                .provider("Star Health")
                .validUntil(LocalDate.of(2028, 1, 1))
                .build();

        Patient patient =
                insuranceService.assignInsuranceToPatient(
                        insurance,
                        1L
                );

        assertThat(patient.getInsurance()).isNotNull();
        assertThat(patient.getInsurance().getPolicyNumber())
                .isEqualTo("TEST-POLICY-101");

        Patient updatedPatient =
                insuranceService.removeInsuranceFromPatient(
                        patient.getId()
                );

        assertThat(updatedPatient.getInsurance()).isNull();
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {

        Appointment appointment = Appointment.builder()
                .appointmentTime(
                        LocalDateTime.now().plusDays(2)
                )
                .reason("Skin Allergy")
                .build();

        Appointment created =
                appointmentService.createNewAppointment(
                        appointment,
                        3L,
                        4L
                );

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus())
                .isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void shouldReassignAppointmentToAnotherDoctor() {

        Appointment appointment = Appointment.builder()
                .appointmentTime(
                        LocalDateTime.now().plusDays(3)
                )
                .reason("Follow up")
                .build();

        Appointment created =
                appointmentService.createNewAppointment(
                        appointment,
                        1L,
                        2L
                );

        Appointment reassigned =
                appointmentService.reassignAppointmentToAnotherDoctor(
                        created.getId(),
                        2L
                );

        assertThat(reassigned.getDoctor().getId())
                .isEqualTo(2L);
    }
}