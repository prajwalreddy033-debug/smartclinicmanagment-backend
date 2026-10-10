package com.smartclinic.dto.billing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillCreateDto {
    private Long clinicId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long appointmentId;

    private BigDecimal discountAmount;
    private BigDecimal taxAmount;

    private String notes;

    @NotEmpty(message = "At least one bill item is required")
    @Valid
    private List<BillItemDto> items;
}
