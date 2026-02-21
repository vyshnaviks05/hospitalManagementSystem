package com.vyshnavi.dev.hospitalManagement;

import com.vyshnavi.dev.hospitalManagement.entity.Appointment;
import com.vyshnavi.dev.hospitalManagement.entity.Insurance;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.service.AppointmentService;
import com.vyshnavi.dev.hospitalManagement.service.InsuranceService;
import org.aspectj.weaver.ast.Var;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
public class InsuranceTests {

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private AppointmentService appointmentService;

    @Test
    public void testInsurance(){
        Insurance insurance=Insurance.builder().policyNumber("HDFC1234").provider("HDFC").validUntil(LocalDate.of(2027,9,1)).build();
        var patient=insuranceService.assignInsuranceToPatient(insurance,1L);
        System.out.println(patient);

        var newPatient=insuranceService.disassociateInsuranceToPatient(patient.getId());
        System.out.println(newPatient);

    }

    @Test
    public void testAppointment(){
        Appointment appointment=Appointment.builder().appointmentTime(LocalDateTime.of(2025,12,6,11,45)).reason("Skin Allergy").build();
        var newAppointment=appointmentService.createNewAppointment(appointment,3L,4L);
        System.out.println(newAppointment);

        var updatedAppointment=appointmentService.reassignAppointmentToAnotherDoctor(newAppointment.getId(), 2L);
        System.out.println(updatedAppointment);
    }
}
