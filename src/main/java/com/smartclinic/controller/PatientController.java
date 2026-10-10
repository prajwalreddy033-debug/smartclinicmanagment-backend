package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.dto.patient.PatientCreateDto;
import com.smartclinic.dto.patient.PatientResponseDto;
import com.smartclinic.service.PatientService;
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
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Endpoints for patient registration, medical history, and lookups")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    @Operation(summary = "List or search patients with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<PatientResponseDto>>> getPatients(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.getPatients(search, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed patient record by ID")
    public ResponseEntity<ApiResponse<PatientResponseDto>> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.getPatientById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'RECEPTIONIST')")
    @Operation(summary = "Register a new patient")
    public ResponseEntity<ApiResponse<PatientResponseDto>> registerPatient(@Valid @RequestBody PatientCreateDto dto) {
        PatientResponseDto created = patientService.registerPatient(dto);
        return ResponseEntity.ok(ApiResponse.ok("Patient registered successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    @Operation(summary = "Update patient record or medical history")
    public ResponseEntity<ApiResponse<PatientResponseDto>> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientCreateDto dto) {
        PatientResponseDto updated = patientService.updatePatient(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Patient updated successfully", updated));
    }
}
