package com.smartclinic.dto.clinic;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDto {
    private Long id;

    @NotBlank(message = "Clinic name is required")
    private String name;

    @NotBlank(message = "Clinic code is required")
    private String code;

    private String address;
    private String phone;
    private String email;
    private String taxId;
    private String status;
    private LocalDateTime createdAt;
    private Long totalDoctors;
    private Long totalPatients;
}
