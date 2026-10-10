package com.smartclinic.dto.appointment;

import com.smartclinic.entity.AppointmentStatus;
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
public class AppointmentResponseDto {
    private Long id;
    private Long clinicId;
    private String clinicName;

    private Long patientId;
    private String patientCode;
    private String patientName;
    private String patientPhone;

    private Long doctorId;
    private String doctorName;
    private String specialization;

    private String appointmentNumber;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String reasonForVisit;
    private AppointmentStatus status;
    private String cancellationReason;
    private Integer tokenNumber;

    private boolean hasConsultation;
    private Long consultationId;

    private LocalDateTime createdAt;
}
