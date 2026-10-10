package com.smartclinic.dto.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponseDto {
    private Long id;
    private Long clinicId;
    private String clinicName;

    private Long appointmentId;
    private String appointmentNumber;

    private Long patientId;
    private String patientCode;
    private String patientName;

    private Long doctorId;
    private String doctorName;
    private String specialization;

    private String symptoms;
    private String diagnosis;
    private String vitalBp;
    private Integer vitalPulse;
    private String vitalTemp;
    private BigDecimal vitalWeight;
    private Integer vitalSpo2;
    private String clinicalNotes;
    private String treatmentPlan;

    private boolean hasPrescription;
    private Long prescriptionId;
    private String prescriptionNumber;

    private LocalDateTime createdAt;
}
