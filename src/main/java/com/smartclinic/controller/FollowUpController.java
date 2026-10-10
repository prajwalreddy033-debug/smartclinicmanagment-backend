package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.dto.followup.FollowUpCreateDto;
import com.smartclinic.dto.followup.FollowUpResponseDto;
import com.smartclinic.entity.FollowUpStatus;
import com.smartclinic.service.FollowUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follow-ups")
@RequiredArgsConstructor
@Tag(name = "Follow-Ups", description = "Endpoints for scheduling and tracking post-consultation follow-ups")
public class FollowUpController {

    private final FollowUpService followUpService;

    @GetMapping
    @Operation(summary = "List scheduled follow-ups with optional filters")
    public ResponseEntity<ApiResponse<PagedResponse<FollowUpResponseDto>>> getFollowUps(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) FollowUpStatus status,
            @PageableDefault(size = 10, sort = "followUpDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(followUpService.getFollowUps(patientId, doctorId, status, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get follow-up record by ID")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> getFollowUpById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(followUpService.getFollowUpById(id)));
    }

    @PostMapping
    @Operation(summary = "Schedule a new patient follow-up")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> createFollowUp(@Valid @RequestBody FollowUpCreateDto dto) {
        FollowUpResponseDto created = followUpService.createFollowUp(dto);
        return ResponseEntity.ok(ApiResponse.ok("Follow-up scheduled successfully", created));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update follow-up status (PENDING, CONFIRMED, COMPLETED, MISSED)")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam FollowUpStatus status) {
        FollowUpResponseDto updated = followUpService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Follow-up status updated to " + status, updated));
    }

    @PostMapping("/{id}/reminder")
    @Operation(summary = "Trigger reminder notification for an upcoming follow-up")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> sendReminder(@PathVariable Long id) {
        FollowUpResponseDto updated = followUpService.sendReminder(id);
        return ResponseEntity.ok(ApiResponse.ok("Reminder sent to patient successfully", updated));
    }
}
