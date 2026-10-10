package com.smartclinic.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {
    private Long id;
    private Long clinicId;
    private String clinicName;
    private Long userId;
    private String patientCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String bloodGroup;
    private String phone;
    private String email;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String allergies;
    private String chronicDiseases;
    private String medicalHistory;
    private LocalDateTime createdAt;
}
