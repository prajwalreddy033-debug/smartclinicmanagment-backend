package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.dto.analytics.DashboardAnalyticsDto;
import com.smartclinic.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for clinic dashboard analytics, KPIs, and charts data")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    @Operation(summary = "Get clinic dashboard analytics, counts, revenue and chart metrics")
    public ResponseEntity<ApiResponse<DashboardAnalyticsDto>> getDashboardAnalytics() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getDashboardAnalytics()));
    }
}
