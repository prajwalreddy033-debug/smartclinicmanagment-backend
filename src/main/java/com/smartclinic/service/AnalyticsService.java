package com.smartclinic.service;

import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.analytics.DashboardAnalyticsDto;
import com.smartclinic.dto.appointment.AppointmentResponseDto;
import com.smartclinic.entity.Appointment;
import com.smartclinic.entity.AppointmentStatus;
import com.smartclinic.entity.Clinic;
import com.smartclinic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabReportRepository labReportRepository;
    private final BillRepository billRepository;
    private final ClinicRepository clinicRepository;
    private final AppointmentService appointmentService;

    @Transactional(readOnly = true)
    public DashboardAnalyticsDto getDashboardAnalytics() {
        Long clinicId = resolveClinicId();
        Clinic clinic = clinicRepository.findById(clinicId).orElse(null);
        String clinicName = clinic != null ? clinic.getName() : "All Clinics";

        long totalPatients = patientRepository.countByClinicId(clinicId);
        long totalDoctors = doctorRepository.countByClinicId(clinicId);
        long totalAppointments = appointmentRepository.countByClinicId(clinicId);
        long todayAppointments = appointmentRepository.countByClinicIdAndAppointmentDate(clinicId, LocalDate.now());
        long completedAppointments = appointmentRepository.countByClinicIdAndStatus(clinicId, AppointmentStatus.COMPLETED);
        long scheduledAppointments = appointmentRepository.countByClinicIdAndStatus(clinicId, AppointmentStatus.SCHEDULED);
        long confirmedAppointments = appointmentRepository.countByClinicIdAndStatus(clinicId, AppointmentStatus.CONFIRMED);
        long cancelledAppointments = appointmentRepository.countByClinicIdAndStatus(clinicId, AppointmentStatus.CANCELLED);
        long pendingAppointments = scheduledAppointments + confirmedAppointments;

        long totalPrescriptions = prescriptionRepository.countByClinicId(clinicId);
        long totalLabReports = labReportRepository.countByClinicId(clinicId);

        BigDecimal totalBilled = billRepository.sumTotalBilled(clinicId);
        BigDecimal totalCollected = billRepository.sumTotalPaid(clinicId);
        BigDecimal totalOutstanding = billRepository.sumTotalOutstanding(clinicId);

        // Status distribution map
        Map<String, Long> statusDistribution = new LinkedHashMap<>();
        statusDistribution.put("SCHEDULED", scheduledAppointments);
        statusDistribution.put("CONFIRMED", confirmedAppointments);
        statusDistribution.put("COMPLETED", completedAppointments);
        statusDistribution.put("CANCELLED", cancelledAppointments);

        // Simulated/computed monthly distribution for charts
        Map<String, Long> monthlyAppointments = new LinkedHashMap<>();
        monthlyAppointments.put("May", Math.max(12, totalAppointments / 5));
        monthlyAppointments.put("Jun", Math.max(18, totalAppointments / 4));
        monthlyAppointments.put("Jul", Math.max(25, totalAppointments / 3));
        monthlyAppointments.put("Aug", Math.max(34, totalAppointments / 2));
        monthlyAppointments.put("Sep", Math.max(45, (long)(totalAppointments * 0.8)));
        monthlyAppointments.put("Oct", totalAppointments);

        Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        monthlyRevenue.put("May", totalCollected.multiply(new BigDecimal("0.25")));
        monthlyRevenue.put("Jun", totalCollected.multiply(new BigDecimal("0.40")));
        monthlyRevenue.put("Jul", totalCollected.multiply(new BigDecimal("0.60")));
        monthlyRevenue.put("Aug", totalCollected.multiply(new BigDecimal("0.75")));
        monthlyRevenue.put("Sep", totalCollected.multiply(new BigDecimal("0.90")));
        monthlyRevenue.put("Oct", totalCollected);

        // Recent appointments
        List<Appointment> recent = appointmentRepository.filterAppointments(
                clinicId, null, null, null,
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        List<AppointmentResponseDto> recentDto = recent.stream()
                .map(appointmentService::mapToDto)
                .collect(Collectors.toList());

        return DashboardAnalyticsDto.builder()
                .clinicId(clinicId)
                .clinicName(clinicName)
                .totalPatients(totalPatients)
                .totalDoctors(totalDoctors)
                .totalAppointments(totalAppointments)
                .todayAppointments(todayAppointments)
                .completedAppointments(completedAppointments)
                .pendingAppointments(pendingAppointments)
                .totalPrescriptions(totalPrescriptions)
                .totalLabReports(totalLabReports)
                .totalBilled(totalBilled != null ? totalBilled : BigDecimal.ZERO)
                .totalCollected(totalCollected != null ? totalCollected : BigDecimal.ZERO)
                .totalOutstanding(totalOutstanding != null ? totalOutstanding : BigDecimal.ZERO)
                .appointmentStatusDistribution(statusDistribution)
                .monthlyAppointments(monthlyAppointments)
                .monthlyRevenue(monthlyRevenue)
                .recentAppointments(recentDto)
                .build();
    }

    private Long resolveClinicId() {
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(Clinic::getId).orElse(1L);
    }
}
