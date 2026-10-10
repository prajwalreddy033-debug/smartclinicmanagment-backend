package com.smartclinic.dto.followup;

import com.smartclinic.entity.FollowUpStatus;
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
public class FollowUpResponseDto {
    private Long id;
    private Long clinicId;
    private String clinicName;

    private Long patientId;
    private String patientCode;
    private String patientName;
    private String patientPhone;

    private Long doctorId;
    private String doctorName;

    private Long appointmentId;

    private LocalDate followUpDate;
    private LocalDate reminderDate;
    private String notes;
    private FollowUpStatus status;
    private Boolean reminderSent;

    private LocalDateTime createdAt;
}
