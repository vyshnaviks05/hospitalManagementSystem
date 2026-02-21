package com.vyshnavi.dev.hospitalManagement.repository;

import com.vyshnavi.dev.hospitalManagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}