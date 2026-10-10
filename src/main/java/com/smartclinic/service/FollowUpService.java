package com.smartclinic.service;

import com.smartclinic.common.PagedResponse;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.followup.FollowUpCreateDto;
import com.smartclinic.dto.followup.FollowUpResponseDto;
import com.smartclinic.entity.*;
import com.smartclinic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PagedResponse<FollowUpResponseDto> getFollowUps(Long patientId, Long doctorId, FollowUpStatus status, Pageable pageable) {
        Page<FollowUp> page;
        if (patientId != null) {
            page = followUpRepository.findByPatientId(patientId, pageable);
        } else if (doctorId != null) {
            page = followUpRepository.findByDoctorId(doctorId, pageable);
        } else if (status != null) {
            Long clinicId = resolveClinicId(null);
            page = followUpRepository.findByClinicIdAndStatus(clinicId, status, pageable);
        } else {
            Long clinicId = resolveClinicId(null);
            page = followUpRepository.findByClinicId(clinicId, pageable);
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
    public FollowUpResponseDto getFollowUpById(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow up not found with id: " + id));
        return mapToDto(followUp);
    }

    @Transactional
    public FollowUpResponseDto createFollowUp(FollowUpCreateDto dto) {
        Long clinicId = resolveClinicId(dto.getClinicId());
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found: " + clinicId));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + dto.getDoctorId()));

        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(dto.getAppointmentId()).orElse(null);
        }

        FollowUp followUp = FollowUp.builder()
                .clinic(clinic)
                .patient(patient)
                .doctor(doctor)
                .appointment(appointment)
                .followUpDate(dto.getFollowUpDate())
                .reminderDate(dto.getReminderDate() != null ? dto.getReminderDate() : dto.getFollowUpDate().minusDays(1))
                .notes(dto.getNotes())
                .status(FollowUpStatus.PENDING)
                .reminderSent(false)
                .build();

        followUp = followUpRepository.save(followUp);

        if (patient.getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(clinic)
                    .user(patient.getUser())
                    .title("Follow-Up Consultation Scheduled")
                    .message("You have a follow-up scheduled with Dr. " + doctor.getUser().getFullName() +
                            " on " + dto.getFollowUpDate() + ".")
                    .type("APPOINTMENT")
                    .build());
        }

        return mapToDto(followUp);
    }

    @Transactional
    public FollowUpResponseDto updateStatus(Long id, FollowUpStatus status) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow up not found: " + id));

        followUp.setStatus(status);
        return mapToDto(followUpRepository.save(followUp));
    }

    @Transactional
    public FollowUpResponseDto sendReminder(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow up not found: " + id));

        followUp.setReminderSent(true);
        followUp = followUpRepository.save(followUp);

        if (followUp.getPatient().getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(followUp.getClinic())
                    .user(followUp.getPatient().getUser())
                    .title("Follow-Up Reminder")
                    .message("Reminder: Your follow-up with Dr. " + followUp.getDoctor().getUser().getFullName() +
                            " is scheduled for tomorrow (" + followUp.getFollowUpDate() + ").")
                    .type("APPOINTMENT")
                    .build());
        }

        return mapToDto(followUp);
    }

    public FollowUpResponseDto mapToDto(FollowUp f) {
        return FollowUpResponseDto.builder()
                .id(f.getId())
                .clinicId(f.getClinic().getId())
                .clinicName(f.getClinic().getName())
                .patientId(f.getPatient().getId())
                .patientCode(f.getPatient().getPatientCode())
                .patientName(f.getPatient().getFullName())
                .patientPhone(f.getPatient().getPhone())
                .doctorId(f.getDoctor().getId())
                .doctorName(f.getDoctor().getUser().getFullName())
                .appointmentId(f.getAppointment() != null ? f.getAppointment().getId() : null)
                .followUpDate(f.getFollowUpDate())
                .reminderDate(f.getReminderDate())
                .notes(f.getNotes())
                .status(f.getStatus())
                .reminderSent(f.getReminderSent())
                .createdAt(f.getCreatedAt())
                .build();
    }

    private Long resolveClinicId(Long explicitClinicId) {
        if (explicitClinicId != null) return explicitClinicId;
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(Clinic::getId).orElse(1L);
    }
}
