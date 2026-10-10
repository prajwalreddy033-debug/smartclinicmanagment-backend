package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.dto.appointment.AppointmentCreateDto;
import com.smartclinic.dto.appointment.AppointmentRescheduleDto;
import com.smartclinic.dto.appointment.AppointmentResponseDto;
import com.smartclinic.entity.AppointmentStatus;
import com.smartclinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Endpoints for appointment booking, rescheduling, and status management")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(summary = "Get list of appointments with multi-criteria filtering")
    public ResponseEntity<ApiResponse<PagedResponse<AppointmentResponseDto>>> getAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {

        PagedResponse<AppointmentResponseDto> response = appointmentService.getAppointments(status, date, doctorId, patientId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment details by ID")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(appointmentService.getAppointmentById(id)));
    }

    @PostMapping
    @Operation(summary = "Book a new clinic appointment")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(@Valid @RequestBody AppointmentCreateDto dto) {
        AppointmentResponseDto created = appointmentService.createAppointment(dto);
        return ResponseEntity.ok(ApiResponse.ok("Appointment booked successfully", created));
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an existing appointment")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRescheduleDto dto) {
        AppointmentResponseDto updated = appointmentService.rescheduleAppointment(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Appointment rescheduled successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status (CONFIRMED, COMPLETED, CANCELLED, NO_SHOW)")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status,
            @RequestParam(required = false) String cancellationReason) {
        AppointmentResponseDto updated = appointmentService.updateStatus(id, status, cancellationReason);
        return ResponseEntity.ok(ApiResponse.ok("Appointment status updated to " + status, updated));
    }
}
