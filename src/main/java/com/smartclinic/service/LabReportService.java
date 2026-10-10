package com.smartclinic.service;

import com.smartclinic.common.PagedResponse;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.lab.LabReportCreateDto;
import com.smartclinic.dto.lab.LabReportResponseDto;
import com.smartclinic.entity.Clinic;
import com.smartclinic.entity.Doctor;
import com.smartclinic.entity.LabReport;
import com.smartclinic.entity.Notification;
import com.smartclinic.entity.Patient;
import com.smartclinic.repository.ClinicRepository;
import com.smartclinic.repository.DoctorRepository;
import com.smartclinic.repository.LabReportRepository;
import com.smartclinic.repository.NotificationRepository;
import com.smartclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabReportService {

    private final LabReportRepository labReportRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PagedResponse<LabReportResponseDto> getLabReports(Long patientId, Pageable pageable) {
        Page<LabReport> page;
        if (patientId != null) {
            page = labReportRepository.findByPatientId(patientId, pageable);
        } else {
            Long clinicId = resolveClinicId(null);
            page = labReportRepository.findByClinicId(clinicId, pageable);
        }

        return new PagedResponse<>(
                page.getContent().stream().map(this::mapToDto).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public LabReportResponseDto getLabReportById(Long id) {
        LabReport report = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab report not found with id: " + id));
        return mapToDto(report);
    }

    @Transactional
    public LabReportResponseDto createLabReport(LabReportCreateDto dto) {
        Long clinicId = resolveClinicId(dto.getClinicId());
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found: " + clinicId));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + dto.getPatientId()));

        Doctor doctor = null;
        if (dto.getDoctorId() != null) {
            doctor = doctorRepository.findById(dto.getDoctorId()).orElse(null);
        }

        String reportNumber = "LAB-" + LocalDate.now().getYear() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LabReport report = LabReport.builder()
                .clinic(clinic)
                .reportNumber(reportNumber)
                .patient(patient)
                .doctor(doctor)
                .testName(dto.getTestName())
                .testCategory(dto.getTestCategory())
                .sampleDate(dto.getSampleDate() != null ? dto.getSampleDate() : LocalDate.now())
                .resultDate(dto.getResultDate() != null ? dto.getResultDate() : LocalDate.now())
                .testResults(dto.getTestResults())
                .fileUrl(dto.getFileUrl())
                .status("COMPLETED")
                .notes(dto.getNotes())
                .build();

        report = labReportRepository.save(report);

        // Notify patient
        if (patient.getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(clinic)
                    .user(patient.getUser())
                    .title("Lab Report Ready")
                    .message("Results for test '" + dto.getTestName() + "' (Report #" + reportNumber + ") are now available.")
                    .type("LAB_REPORT")
                    .build());
        }

        return mapToDto(report);
    }

    @Transactional
    public String uploadLabFile(MultipartFile file) throws IOException {
        String uploadDir = "./uploads/lab-reports";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        Path targetPath = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/lab-reports/" + filename;
    }

    public LabReportResponseDto mapToDto(LabReport r) {
        return LabReportResponseDto.builder()
                .id(r.getId())
                .clinicId(r.getClinic().getId())
                .clinicName(r.getClinic().getName())
                .reportNumber(r.getReportNumber())
                .patientId(r.getPatient().getId())
                .patientCode(r.getPatient().getPatientCode())
                .patientName(r.getPatient().getFullName())
                .doctorId(r.getDoctor() != null ? r.getDoctor().getId() : null)
                .doctorName(r.getDoctor() != null ? r.getDoctor().getUser().getFullName() : null)
                .testName(r.getTestName())
                .testCategory(r.getTestCategory())
                .sampleDate(r.getSampleDate())
                .resultDate(r.getResultDate())
                .testResults(r.getTestResults())
                .fileUrl(r.getFileUrl())
                .status(r.getStatus())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private Long resolveClinicId(Long explicitClinicId) {
        if (explicitClinicId != null) return explicitClinicId;
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(Clinic::getId).orElse(1L);
    }
}
