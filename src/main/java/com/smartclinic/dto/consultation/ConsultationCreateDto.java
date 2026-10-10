package com.smartclinic.dto.consultation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationCreateDto {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotBlank(message = "Symptoms description is required")
    private String symptoms;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String vitalBp;
    private Integer vitalPulse;
    private String vitalTemp;
    private BigDecimal vitalWeight;
    private Integer vitalSpo2;

    private String clinicalNotes;
    private String treatmentPlan;
}
