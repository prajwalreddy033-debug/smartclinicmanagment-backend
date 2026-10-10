package com.smartclinic.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreateDto {
    private Long clinicId; // optional, defaults to active clinic context

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private String bloodGroup;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String email;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String allergies;
    private String chronicDiseases;
    private String medicalHistory;
}
