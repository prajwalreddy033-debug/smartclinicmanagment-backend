package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.dto.doctor.DoctorDto;
import com.smartclinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Endpoints for doctor schedules, specializations, and fee information")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "List doctors, optionally filtered by specialization")
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getDoctors(@RequestParam(required = false) String specialization) {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getDoctors(specialization)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor details by ID")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getDoctorById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get doctor details by User account ID")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getDoctorByUserId(userId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'DOCTOR')")
    @Operation(summary = "Update doctor profile and available days/hours")
    public ResponseEntity<ApiResponse<DoctorDto>> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDto dto) {
        DoctorDto updated = doctorService.updateDoctorProfile(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Doctor profile updated successfully", updated));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    @Operation(summary = "Create a new doctor account (admin only)")
    public ResponseEntity<ApiResponse<DoctorDto>> createDoctor(@Valid @RequestBody DoctorDto dto) {
        DoctorDto created = doctorService.createDoctor(dto);
        return ResponseEntity.ok(ApiResponse.ok("Doctor created successfully. Default password: Doctor@123", created));
    }
}
