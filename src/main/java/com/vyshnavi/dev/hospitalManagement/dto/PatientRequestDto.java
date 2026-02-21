package com.vyshnavi.dev.hospitalManagement.dto;

import com.vyshnavi.dev.hospitalManagement.entity.type.BloodGroupType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDto {

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotNull(message = "birthDate is required")
    @Past(message = "birthDate must be in the past")
    private LocalDate birthDate;

    @Email(message = "must be a well-formed email address")
    private String email;

    private String gender;

    @NotNull(message = "bloodGroup is required")
    private BloodGroupType bloodGroup;
}
