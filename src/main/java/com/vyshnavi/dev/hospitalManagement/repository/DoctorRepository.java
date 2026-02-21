package com.vyshnavi.dev.hospitalManagement.repository;

import com.vyshnavi.dev.hospitalManagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}