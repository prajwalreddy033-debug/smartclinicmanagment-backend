package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.appointment.AppointmentCreateDto;
import com.smartclinic.dto.appointment.AppointmentRescheduleDto;
import com.smartclinic.dto.appointment.AppointmentResponseDto;
import com.smartclinic.entity.*;
import com.smartclinic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PagedResponse<AppointmentResponseDto> getAppointments(
            AppointmentStatus status,
            LocalDate date,
            Long doctorId,
            Long patientId,
            Pageable pageable) {

        Page<Appointment> page;
        if (patientId != null) {
            page = appointmentRepository.findByPatientId(patientId, pageable);
        } else if (doctorId != null && status == null && date == null) {
            page = appointmentRepository.findByDoctorId(doctorId, pageable);
        } else {
            Long clinicId = resolveClinicId(null);
            page = appointmentRepository.filterAppointments(clinicId, status, date, doctorId, pageable);
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
    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return mapToDto(apt);
    }

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentCreateDto dto) {
        Long clinicId = resolveClinicId(dto.getClinicId());
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        // Calculate token number for the day
        List<Appointment> existingForDay = appointmentRepository.findByDoctorIdAndAppointmentDate(doctor.getId(), dto.getAppointmentDate());
        int nextToken = existingForDay.size() + 1;

        String aptNum = "APT-" + dto.getAppointmentDate().getYear() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Appointment appointment = Appointment.builder()
                .clinic(clinic)
                .patient(patient)
                .doctor(doctor)
                .appointmentNumber(aptNum)
                .appointmentDate(dto.getAppointmentDate())
                .timeSlot(dto.getTimeSlot())
                .reasonForVisit(dto.getReasonForVisit())
                .status(AppointmentStatus.SCHEDULED)
                .tokenNumber(nextToken)
                .build();

        appointment = appointmentRepository.save(appointment);

        // Notify patient if user account is attached
        if (patient.getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(clinic)
                    .user(patient.getUser())
                    .title("Appointment Scheduled")
                    .message("Your appointment (" + aptNum + ") with " + doctor.getUser().getFullName() +
                            " on " + dto.getAppointmentDate() + " at " + dto.getTimeSlot() + " is confirmed.")
                    .type("APPOINTMENT")
                    .build());
        }

        // Notify doctor
        notificationRepository.save(Notification.builder()
                .clinic(clinic)
                .user(doctor.getUser())
                .title("New Appointment Booked")
                .message("Patient " + patient.getFullName() + " booked an appointment on " +
                        dto.getAppointmentDate() + " at " + dto.getTimeSlot() + " (Token: #" + nextToken + ").")
                .type("APPOINTMENT")
                .build());

        return mapToDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto rescheduleAppointment(Long id, AppointmentRescheduleDto dto) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if (apt.getStatus() == AppointmentStatus.CANCELLED || apt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot reschedule an appointment that is already " + apt.getStatus());
        }

        apt.setAppointmentDate(dto.getAppointmentDate());
        apt.setTimeSlot(dto.getTimeSlot());
        apt.setStatus(AppointmentStatus.CONFIRMED);

        apt = appointmentRepository.save(apt);

        if (apt.getPatient().getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(apt.getClinic())
                    .user(apt.getPatient().getUser())
                    .title("Appointment Rescheduled")
                    .message("Your appointment " + apt.getAppointmentNumber() + " has been rescheduled to " +
                            dto.getAppointmentDate() + " at " + dto.getTimeSlot() + ".")
                    .type("APPOINTMENT")
                    .build());
        }

        return mapToDto(apt);
    }

    @Transactional
    public AppointmentResponseDto updateStatus(Long id, AppointmentStatus status, String cancellationReason) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        apt.setStatus(status);
        if (cancellationReason != null) {
            apt.setCancellationReason(cancellationReason);
        }

        return mapToDto(appointmentRepository.save(apt));
    }

    public AppointmentResponseDto mapToDto(Appointment apt) {
        Optional<Consultation> cons = consultationRepository.findByAppointmentId(apt.getId());

        return AppointmentResponseDto.builder()
                .id(apt.getId())
                .clinicId(apt.getClinic().getId())
                .clinicName(apt.getClinic().getName())
                .patientId(apt.getPatient().getId())
                .patientCode(apt.getPatient().getPatientCode())
                .patientName(apt.getPatient().getFullName())
                .patientPhone(apt.getPatient().getPhone())
                .doctorId(apt.getDoctor().getId())
                .doctorName(apt.getDoctor().getUser().getFullName())
                .specialization(apt.getDoctor().getSpecialization())
                .appointmentNumber(apt.getAppointmentNumber())
                .appointmentDate(apt.getAppointmentDate())
                .timeSlot(apt.getTimeSlot())
                .reasonForVisit(apt.getReasonForVisit())
                .status(apt.getStatus())
                .cancellationReason(apt.getCancellationReason())
                .tokenNumber(apt.getTokenNumber())
                .hasConsultation(cons.isPresent())
                .consultationId(cons.map(Consultation::getId).orElse(null))
                .createdAt(apt.getCreatedAt())
                .build();
    }

    private Long resolveClinicId(Long explicitClinicId) {
        if (explicitClinicId != null) return explicitClinicId;
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(Clinic::getId).orElse(1L);
    }
}
