package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.dto.clinic.ClinicDto;
import com.smartclinic.service.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinics", description = "Endpoints for managing medical clinic branches")
public class ClinicController {

    private final ClinicService clinicService;

    @GetMapping
    @Operation(summary = "List all clinic branches")
    public ResponseEntity<ApiResponse<List<ClinicDto>>> getAllClinics() {
        return ResponseEntity.ok(ApiResponse.ok(clinicService.getAllClinics()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get clinic branch details by ID")
    public ResponseEntity<ApiResponse<ClinicDto>> getClinicById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clinicService.getClinicById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    @Operation(summary = "Create a new clinic branch (Admin only)")
    public ResponseEntity<ApiResponse<ClinicDto>> createClinic(@Valid @RequestBody ClinicDto dto) {
        ClinicDto created = clinicService.createClinic(dto);
        return ResponseEntity.ok(ApiResponse.ok("Clinic registered successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    @Operation(summary = "Update clinic branch details")
    public ResponseEntity<ApiResponse<ClinicDto>> updateClinic(@PathVariable Long id, @Valid @RequestBody ClinicDto dto) {
        ClinicDto updated = clinicService.updateClinic(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Clinic updated successfully", updated));
    }
}
