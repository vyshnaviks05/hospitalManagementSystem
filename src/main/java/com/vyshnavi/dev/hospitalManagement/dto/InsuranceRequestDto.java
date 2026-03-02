package com.vyshnavi.dev.hospitalManagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceRequestDto {

    @NotBlank(message = "policyNumber must not be blank")
    private String policyNumber;

    @NotBlank(message = "provider must not be blank")
    private String provider;

    @NotNull(message = "validUntil is required")
    @Future(message = "validUntil must be a future date")
    private LocalDate validUntil;
}
