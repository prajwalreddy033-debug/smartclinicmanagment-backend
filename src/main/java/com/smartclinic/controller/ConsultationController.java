package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.dto.consultation.ConsultationCreateDto;
import com.smartclinic.dto.consultation.ConsultationResponseDto;
import com.smartclinic.service.ConsultationService;
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
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
@Tag(name = "Consultations", description = "Endpoints for doctor clinical notes, patient vitals, and diagnosis")
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping
    @Operation(summary = "List consultation records with optional patient or doctor filters")
    public ResponseEntity<ApiResponse<PagedResponse<ConsultationResponseDto>>> getConsultations(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(consultationService.getConsultations(patientId, doctorId, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get consultation details by ID")
    public ResponseEntity<ApiResponse<ConsultationResponseDto>> getConsultationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(consultationService.getConsultationById(id)));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get consultation by associated appointment ID")
    public ResponseEntity<ApiResponse<ConsultationResponseDto>> getConsultationByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(ApiResponse.ok(consultationService.getConsultationByAppointmentId(appointmentId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'DOCTOR')")
    @Operation(summary = "Record clinical encounter, vitals, diagnosis and treatment plan")
    public ResponseEntity<ApiResponse<ConsultationResponseDto>> createConsultation(@Valid @RequestBody ConsultationCreateDto dto) {
        ConsultationResponseDto created = consultationService.createConsultation(dto);
        return ResponseEntity.ok(ApiResponse.ok("Consultation recorded successfully", created));
    }
}
