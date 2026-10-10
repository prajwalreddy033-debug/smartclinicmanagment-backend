package com.smartclinic.dto.lab;

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
public class LabReportCreateDto {
    private Long clinicId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long doctorId;

    @NotBlank(message = "Test name is required")
    private String testName;

    private String testCategory;
    private LocalDate sampleDate;
    private LocalDate resultDate;
    private String testResults;
    private String fileUrl;
    private String notes;
}
