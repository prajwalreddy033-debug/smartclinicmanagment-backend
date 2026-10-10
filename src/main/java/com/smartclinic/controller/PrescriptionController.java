package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.dto.prescription.PrescriptionCreateDto;
import com.smartclinic.dto.prescription.PrescriptionResponseDto;
import com.smartclinic.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescriptions", description = "Endpoints for electronic prescriptions and PDF download")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List prescriptions with optional patient or doctor filters")
    public ResponseEntity<ApiResponse<PagedResponse<PrescriptionResponseDto>>> getPrescriptions(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(prescriptionService.getPrescriptions(patientId, doctorId, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get prescription details by ID")
    public ResponseEntity<ApiResponse<PrescriptionResponseDto>> getPrescriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(prescriptionService.getPrescriptionById(id)));
    }

    @GetMapping("/consultation/{consultationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get prescription by consultation ID")
    public ResponseEntity<ApiResponse<PrescriptionResponseDto>> getPrescriptionByConsultationId(@PathVariable Long consultationId) {
        return ResponseEntity.ok(ApiResponse.ok(prescriptionService.getPrescriptionByConsultationId(consultationId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Create an electronic prescription with medication items")
    public ResponseEntity<ApiResponse<PrescriptionResponseDto>> createPrescription(@Valid @RequestBody PrescriptionCreateDto dto) {
        PrescriptionResponseDto created = prescriptionService.createPrescription(dto);
        return ResponseEntity.ok(ApiResponse.ok("Prescription generated successfully", created));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Download or stream the generated PDF prescription document")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(@PathVariable Long id) {
        byte[] pdfBytes = prescriptionService.downloadPrescriptionPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
