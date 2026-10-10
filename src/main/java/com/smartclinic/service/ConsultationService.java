package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.consultation.ConsultationCreateDto;
import com.smartclinic.dto.consultation.ConsultationResponseDto;
import com.smartclinic.entity.Appointment;
import com.smartclinic.entity.AppointmentStatus;
import com.smartclinic.entity.Consultation;
import com.smartclinic.entity.Prescription;
import com.smartclinic.repository.AppointmentRepository;
import com.smartclinic.repository.ClinicRepository;
import com.smartclinic.repository.ConsultationRepository;
import com.smartclinic.repository.DoctorRepository;
import com.smartclinic.repository.PatientRepository;
import com.smartclinic.repository.UserRepository;
import com.smartclinic.entity.Role;
import com.smartclinic.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import com.smartclinic.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PagedResponse<ConsultationResponseDto> getConsultations(Long patientId, Long doctorId, Pageable pageable) {
        User user = currentUser();
        Page<Consultation> page;
        if (user.getRole() == Role.PATIENT) {
            Long ownPatientId = patientRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Patient profile not found")).getId();
            page = consultationRepository.findByPatientId(ownPatientId, pageable);
        } else if (user.getRole() == Role.DOCTOR) {
            Long ownDoctorId = doctorRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found")).getId();
            page = consultationRepository.findByDoctorId(ownDoctorId, pageable);
        } else {
            page = consultationRepository.findByClinicId(requireClinic(user), pageable);
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
    public ConsultationResponseDto getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + id));
        authorize(consultation);
        return mapToDto(consultation);
    }

    @Transactional(readOnly = true)
    public ConsultationResponseDto getConsultationByAppointmentId(Long appointmentId) {
        Consultation consultation = consultationRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found for appointment: " + appointmentId));
        authorize(consultation);
        return mapToDto(consultation);
    }

    @Transactional
    public ConsultationResponseDto createConsultation(ConsultationCreateDto dto) {
        User user = currentUser();
        Appointment apt = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + dto.getAppointmentId()));
        if (user.getRole() == Role.DOCTOR) {
            Long ownDoctorId = doctorRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found")).getId();
            if (!ownDoctorId.equals(apt.getDoctor().getId())) throw new BadRequestException("You can create consultations only for your appointments");
        }
        if (user.getClinic() == null || !user.getClinic().getId().equals(apt.getClinic().getId())) throw new BadRequestException("Appointment belongs to another clinic");

        if (consultationRepository.findByAppointmentId(apt.getId()).isPresent()) {
            throw new BadRequestException("Consultation already exists for appointment: " + apt.getAppointmentNumber());
        }

        Consultation consultation = Consultation.builder()
                .clinic(apt.getClinic())
                .appointment(apt)
                .patient(apt.getPatient())
                .doctor(apt.getDoctor())
                .symptoms(dto.getSymptoms())
                .diagnosis(dto.getDiagnosis())
                .vitalBp(dto.getVitalBp())
                .vitalPulse(dto.getVitalPulse())
                .vitalTemp(dto.getVitalTemp())
                .vitalWeight(dto.getVitalWeight())
                .vitalSpo2(dto.getVitalSpo2())
                .clinicalNotes(dto.getClinicalNotes())
                .treatmentPlan(dto.getTreatmentPlan())
                .build();

        consultation = consultationRepository.save(consultation);

        // Mark appointment as COMPLETED
        apt.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(apt);

        return mapToDto(consultation);
    }

    public ConsultationResponseDto mapToDto(Consultation c) {
        Optional<Prescription> rx = prescriptionRepository.findByConsultationId(c.getId());

        return ConsultationResponseDto.builder()
                .id(c.getId())
                .clinicId(c.getClinic().getId())
                .clinicName(c.getClinic().getName())
                .appointmentId(c.getAppointment().getId())
                .appointmentNumber(c.getAppointment().getAppointmentNumber())
                .patientId(c.getPatient().getId())
                .patientCode(c.getPatient().getPatientCode())
                .patientName(c.getPatient().getFullName())
                .doctorId(c.getDoctor().getId())
                .doctorName(c.getDoctor().getUser().getFullName())
                .specialization(c.getDoctor().getSpecialization())
                .symptoms(c.getSymptoms())
                .diagnosis(c.getDiagnosis())
                .vitalBp(c.getVitalBp())
                .vitalPulse(c.getVitalPulse())
                .vitalTemp(c.getVitalTemp())
                .vitalWeight(c.getVitalWeight())
                .vitalSpo2(c.getVitalSpo2())
                .clinicalNotes(c.getClinicalNotes())
                .treatmentPlan(c.getTreatmentPlan())
                .hasPrescription(rx.isPresent())
                .prescriptionId(rx.map(Prescription::getId).orElse(null))
                .prescriptionNumber(rx.map(Prescription::getPrescriptionNumber).orElse(null))
                .createdAt(c.getCreatedAt())
                .build();
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Long requireClinic(User user) {
        if (user.getClinic() == null) throw new BadRequestException("User is not assigned to a clinic");
        return user.getClinic().getId();
    }

    private void authorize(Consultation c) {
        User user = currentUser();
        if (user.getRole() == Role.PATIENT) {
            Long patientId = patientRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Patient profile not found")).getId();
            if (!patientId.equals(c.getPatient().getId())) throw new ResourceNotFoundException("Consultation not found");
        } else if (user.getRole() == Role.DOCTOR) {
            Long doctorId = doctorRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found")).getId();
            if (!doctorId.equals(c.getDoctor().getId())) throw new ResourceNotFoundException("Consultation not found");
        } else if (user.getClinic() == null || !user.getClinic().getId().equals(c.getClinic().getId())) {
            throw new ResourceNotFoundException("Consultation not found");
        }
    }

    private Long resolveClinicId() {
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(c -> c.getId()).orElse(1L);
    }
}
