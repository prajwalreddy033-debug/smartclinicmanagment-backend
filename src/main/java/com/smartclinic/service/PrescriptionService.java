package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.dto.prescription.PrescriptionCreateDto;
import com.smartclinic.dto.prescription.PrescriptionItemDto;
import com.smartclinic.dto.prescription.PrescriptionResponseDto;
import com.smartclinic.entity.*;
import com.smartclinic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final NotificationRepository notificationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PdfExportService pdfExportService;

    @Transactional(readOnly = true)
    public PagedResponse<PrescriptionResponseDto> getPrescriptions(Long patientId, Long doctorId, Pageable pageable) {
        User user = currentUser();
        Page<Prescription> page;
        if (user.getRole() == Role.PATIENT) {
            Patient patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
            page = prescriptionRepository.findByPatientId(patient.getId(), pageable);
        } else if (user.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
            page = prescriptionRepository.findByDoctorId(doctor.getId(), pageable);
        } else {
            Long clinicId = requireClinic(user);
            page = prescriptionRepository.findByClinicId(clinicId, pageable);
        }
        return toPage(page);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponseDto getPrescriptionById(Long id) {
        Prescription rx = findAndAuthorize(id);
        return mapToDto(rx);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponseDto getPrescriptionByConsultationId(Long consultationId) {
        Prescription rx = prescriptionRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for consultation: " + consultationId));
        authorize(rx);
        return mapToDto(rx);
    }

    @Transactional
    public PrescriptionResponseDto createPrescription(PrescriptionCreateDto dto) {
        User user = currentUser();
        if (user.getRole() != Role.DOCTOR) {
            throw new BadRequestException("Only the treating doctor can create a prescription");
        }
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found: " + dto.getConsultationId()));

        if (!doctor.getId().equals(consultation.getDoctor().getId())) {
            throw new BadRequestException("You can prescribe only for your own consultations");
        }
        if (!doctor.getClinic().getId().equals(consultation.getClinic().getId())) {
            throw new BadRequestException("Doctor and consultation belong to different clinics");
        }
        if (prescriptionRepository.findByConsultationId(consultation.getId()).isPresent()) {
            throw new BadRequestException("Prescription already generated for this consultation");
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("At least one medication is required");
        }

        String rxNumber = "RX-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Prescription prescription = Prescription.builder()
                .clinic(consultation.getClinic()).consultation(consultation)
                .patient(consultation.getPatient()).doctor(consultation.getDoctor())
                .prescriptionNumber(rxNumber).generalAdvice(dto.getGeneralAdvice())
                .dietInstructions(dto.getDietInstructions()).nextVisitDate(dto.getNextVisitDate()).build();

        for (PrescriptionItemDto itemDto : dto.getItems()) {
            prescription.addItem(PrescriptionItem.builder()
                    .medicineName(itemDto.getMedicineName().trim()).dosage(itemDto.getDosage().trim())
                    .frequency(itemDto.getFrequency().trim()).duration(itemDto.getDuration().trim())
                    .instructions(itemDto.getInstructions()).build());
        }
        prescription = prescriptionRepository.save(prescription);

        if (consultation.getPatient().getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(consultation.getClinic()).user(consultation.getPatient().getUser())
                    .title("Prescription Ready")
                    .message("Dr. " + consultation.getDoctor().getUser().getFullName() + " has prepared prescription " + rxNumber + ".")
                    .type("PRESCRIPTION").build());
        }
        return mapToDto(prescription);
    }

    @Transactional(readOnly = true)
    public byte[] downloadPrescriptionPdf(Long prescriptionId) {
        Prescription rx = findAndAuthorize(prescriptionId);
        return pdfExportService.generatePrescriptionPdf(rx);
    }

    private Prescription findAndAuthorize(Long id) {
        Prescription rx = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        authorize(rx);
        return rx;
    }

    private void authorize(Prescription rx) {
        User user = currentUser();
        if (user.getRole() == Role.PATIENT) {
            if (rx.getPatient().getUser() == null || !rx.getPatient().getUser().getId().equals(user.getId()))
                throw new ResourceNotFoundException("Prescription not found");
            return;
        }
        if (user.getRole() == Role.DOCTOR) {
            if (rx.getDoctor().getUser() == null || !rx.getDoctor().getUser().getId().equals(user.getId()))
                throw new ResourceNotFoundException("Prescription not found");
            return;
        }
        if (!Role.CLINIC_ADMIN.equals(user.getRole()) && !Role.RECEPTIONIST.equals(user.getRole()))
            throw new ResourceNotFoundException("Prescription not found");
        if (user.getClinic() == null || !user.getClinic().getId().equals(rx.getClinic().getId()))
            throw new ResourceNotFoundException("Prescription not found");
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new ResourceNotFoundException("Authenticated user not found");
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Long requireClinic(User user) {
        if (user.getClinic() == null) throw new BadRequestException("User is not assigned to a clinic");
        return user.getClinic().getId();
    }

    private PagedResponse<PrescriptionResponseDto> toPage(Page<Prescription> page) {
        return new PagedResponse<>(page.getContent().stream().map(this::mapToDto).toList(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public PrescriptionResponseDto mapToDto(Prescription rx) {
        Integer age = null;
        if (rx.getPatient().getDateOfBirth() != null) age = Period.between(rx.getPatient().getDateOfBirth(), LocalDate.now()).getYears();
        return PrescriptionResponseDto.builder().id(rx.getId()).clinicId(rx.getClinic().getId()).clinicName(rx.getClinic().getName())
                .prescriptionNumber(rx.getPrescriptionNumber()).consultationId(rx.getConsultation().getId())
                .patientId(rx.getPatient().getId()).patientCode(rx.getPatient().getPatientCode()).patientName(rx.getPatient().getFullName())
                .patientAge(age).patientGender(rx.getPatient().getGender()).doctorId(rx.getDoctor().getId())
                .doctorName(rx.getDoctor().getUser().getFullName()).doctorSpecialization(rx.getDoctor().getSpecialization())
                .doctorLicense(rx.getDoctor().getLicenseNumber()).generalAdvice(rx.getGeneralAdvice()).dietInstructions(rx.getDietInstructions())
                .nextVisitDate(rx.getNextVisitDate()).items(rx.getItems().stream().map(i -> PrescriptionItemDto.builder().id(i.getId())
                        .medicineName(i.getMedicineName()).dosage(i.getDosage()).frequency(i.getFrequency()).duration(i.getDuration())
                        .instructions(i.getInstructions()).build()).toList()).createdAt(rx.getCreatedAt()).build();
    }
}
