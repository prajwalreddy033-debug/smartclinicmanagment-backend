package com.smartclinic.dto.followup;

import jakarta.validation.constraints.FutureOrPresent;
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
public class FollowUpCreateDto {
    private Long clinicId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long appointmentId;

    @NotNull(message = "Follow-up date is required")
    @FutureOrPresent(message = "Follow-up date cannot be in the past")
    private LocalDate followUpDate;

    private LocalDate reminderDate;
    private String notes;
}
