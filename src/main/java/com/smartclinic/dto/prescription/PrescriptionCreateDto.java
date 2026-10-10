package com.smartclinic.dto.prescription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionCreateDto {
    @NotNull(message = "Consultation ID is required")
    private Long consultationId;

    private String generalAdvice;
    private String dietInstructions;
    private LocalDate nextVisitDate;

    @NotEmpty(message = "At least one medication item is required")
    @Valid
    private List<PrescriptionItemDto> items;
}
