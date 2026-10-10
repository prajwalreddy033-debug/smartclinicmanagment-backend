package com.smartclinic.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private Long id;
    private Long clinicId;
    private String clinicName;
    private Long userId;

    private String fullName;
    private String email;
    private String phone;

    private String doctorCode;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "Consultation fee is required")
    private BigDecimal consultationFee;

    private Integer experienceYears;
    private String availableDays;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private String status;
}
