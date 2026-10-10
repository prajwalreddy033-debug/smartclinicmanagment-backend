package com.smartclinic.dto.prescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseDto {
    private Long id;
    private Long clinicId;
    private String clinicName;

    private String prescriptionNumber;
    private Long consultationId;

    private Long patientId;
    private String patientCode;
    private String patientName;
    private Integer patientAge;
    private String patientGender;

    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private String doctorLicense;

    private String generalAdvice;
    private String dietInstructions;
    private LocalDate nextVisitDate;

    private List<PrescriptionItemDto> items;

    private LocalDateTime createdAt;
}
