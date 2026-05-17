package com.vyshnavi.dev.hospitalManagement.dto;

import com.vyshnavi.dev.hospitalManagement.entity.type.BloodGroupType;
import com.vyshnavi.dev.hospitalManagement.entity.type.GenderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {

    private Long id;
    private String name;
    private LocalDate birthDate;
    private String email;
    private GenderType gender;
    private BloodGroupType bloodGroup;
}