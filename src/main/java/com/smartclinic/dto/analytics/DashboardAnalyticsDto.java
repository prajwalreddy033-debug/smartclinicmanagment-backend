package com.smartclinic.dto.analytics;

import com.smartclinic.dto.appointment.AppointmentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsDto {
    private Long clinicId;
    private String clinicName;

    // Counts
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long todayAppointments;
    private long completedAppointments;
    private long pendingAppointments;
    private long totalPrescriptions;
    private long totalLabReports;

    // Financials
    private BigDecimal totalBilled;
    private BigDecimal totalCollected;
    private BigDecimal totalOutstanding;

    // Chart Data
    private Map<String, Long> appointmentStatusDistribution;
    private Map<String, Long> monthlyAppointments;
    private Map<String, BigDecimal> monthlyRevenue;

    // Recent items
    private List<AppointmentResponseDto> recentAppointments;
}
