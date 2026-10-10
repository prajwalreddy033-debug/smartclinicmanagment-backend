package com.smartclinic.dto.lab;

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
public class LabReportResponseDto {
    private Long id;
    private Long clinicId;
    private String clinicName;

    private String reportNumber;
    private Long patientId;
    private String patientCode;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private String testName;
    private String testCategory;
    private LocalDate sampleDate;
    private LocalDate resultDate;
    private String testResults;
    private String fileUrl;
    private String status;
    private String notes;

    private LocalDateTime createdAt;
}
