package com.smartclinic.dto.billing;

import com.smartclinic.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDto {
    private Long id;
    private Long clinicId;
    private String clinicName;

    private String invoiceNumber;
    private Long patientId;
    private String patientCode;
    private String patientName;
    private String patientPhone;

    private Long appointmentId;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String notes;

    private List<BillItemDto> items;

    private LocalDateTime createdAt;
}
