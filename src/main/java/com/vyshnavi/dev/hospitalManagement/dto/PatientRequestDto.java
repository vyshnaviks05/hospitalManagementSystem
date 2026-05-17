package com.vyshnavi.dev.hospitalManagement.dto;

import com.vyshnavi.dev.hospitalManagement.entity.type.BloodGroupType;
import com.vyshnavi.dev.hospitalManagement.entity.type.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDto {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotNull(message = "Gender is required")
    private GenderType gender;

    @NotNull(message = "Blood group is required")
    private BloodGroupType bloodGroup;
}