package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.dto.billing.BillCreateDto;
import com.smartclinic.dto.billing.BillPaymentDto;
import com.smartclinic.dto.billing.BillResponseDto;
import com.smartclinic.entity.PaymentStatus;
import com.smartclinic.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Endpoints for invoices, line items, and payment processing")
public class BillingController {

    private final BillingService billingService;

    @GetMapping
    @Operation(summary = "List invoices, optionally filtered by patient or payment status")
    public ResponseEntity<ApiResponse<PagedResponse<BillResponseDto>>> getBills(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.getBills(patientId, status, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice details and line items by ID")
    public ResponseEntity<ApiResponse<BillResponseDto>> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.getBillById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'RECEPTIONIST')")
    @Operation(summary = "Generate a new clinic invoice")
    public ResponseEntity<ApiResponse<BillResponseDto>> createBill(@Valid @RequestBody BillCreateDto dto) {
        BillResponseDto created = billingService.createBill(dto);
        return ResponseEntity.ok(ApiResponse.ok("Invoice generated successfully", created));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'RECEPTIONIST')")
    @Operation(summary = "Record a payment against an invoice")
    public ResponseEntity<ApiResponse<BillResponseDto>> recordPayment(
            @PathVariable Long id,
            @Valid @RequestBody BillPaymentDto dto) {
        BillResponseDto updated = billingService.recordPayment(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Payment recorded successfully", updated));
    }
}
