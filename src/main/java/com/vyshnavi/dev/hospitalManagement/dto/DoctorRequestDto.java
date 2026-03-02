package com.vyshnavi.dev.hospitalManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequestDto {

    @NotBlank(message = "name must not be blank")
    private String name;

    private String specialization;

    @NotBlank(message = "email must not be blank")
    @Email(message = "must be a well-formed email address")
    private String email;
}
