package com.vyshnavi.dev.hospitalManagement.repository;

import com.vyshnavi.dev.hospitalManagement.dto.BloodGroupCountDto;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.entity.type.BloodGroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT new com.vyshnavi.dev.hospitalManagement.dto.BloodGroupCountDto(p.bloodGroup, COUNT(p)) " +
            "FROM Patient p GROUP BY p.bloodGroup")
    List<BloodGroupCountDto> countEachBloodGroupType();

    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments")
    List<Patient> findAllPatientsWithAppointments();

    boolean existsByEmail(String email);

    Page<Patient> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );
}
