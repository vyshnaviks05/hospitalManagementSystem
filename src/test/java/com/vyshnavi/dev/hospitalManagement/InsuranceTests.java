package com.vyshnavi.dev.hospitalManagement;

import com.vyshnavi.dev.hospitalManagement.entity.Appointment;
import com.vyshnavi.dev.hospitalManagement.entity.Insurance;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.service.AppointmentService;
import com.vyshnavi.dev.hospitalManagement.service.InsuranceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class InsuranceTests {

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private AppointmentService appointmentService;

    @Test
    public void testAssignAndRemoveInsurance() {
        Insurance insurance = Insurance.builder()
                .policyNumber("HDFC1234")
                .provider("HDFC")
                .validUntil(LocalDate.of(2027, 9, 1))
                .build();

        Patient patientWithInsurance = insuranceService.assignInsuranceToPatient(insurance, 1L);
        assertThat(patientWithInsurance.getInsurance()).isNotNull();
        System.out.println(patientWithInsurance);

        Patient patientWithoutInsurance = insuranceService.removeInsuranceFromPatient(patientWithInsurance.getId());
        assertThat(patientWithoutInsurance.getInsurance()).isNull();
        System.out.println(patientWithoutInsurance);
    }

    @Test
    public void testCreateAndReassignAppointment() {
        Appointment appointment = Appointment.builder()
                .appointmentTime(LocalDateTime.of(2025, 12, 6, 11, 45))
                .reason("Skin Allergy")
                .build();

        Appointment created = appointmentService.createNewAppointment(appointment, 3L, 4L);
        assertThat(created.getId()).isNotNull();
        System.out.println(created);

        Appointment reassigned = appointmentService.reassignAppointmentToAnotherDoctor(created.getId(), 2L);
        assertThat(reassigned.getDoctor().getId()).isEqualTo(2L);
        System.out.println(reassigned);
    }
}
