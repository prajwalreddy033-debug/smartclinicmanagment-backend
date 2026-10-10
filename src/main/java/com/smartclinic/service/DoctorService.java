package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.doctor.DoctorDto;
import com.smartclinic.entity.Clinic;
import com.smartclinic.entity.Doctor;
import com.smartclinic.entity.Role;
import com.smartclinic.entity.User;
import com.smartclinic.repository.ClinicRepository;
import com.smartclinic.repository.DoctorRepository;
import com.smartclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<DoctorDto> getDoctors(String specialization) {
        Long clinicId = resolveClinicId();
        List<Doctor> doctors;

        if (specialization != null && !specialization.trim().isEmpty()) {
            doctors = doctorRepository.findByClinicIdAndSpecialization(clinicId, specialization.trim());
        } else {
            doctors = doctorRepository.findByClinicId(clinicId);
        }

        return doctors.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return mapToDto(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorDto getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + userId));
        return mapToDto(doctor);
    }

    @Transactional
    public DoctorDto updateDoctorProfile(Long id, DoctorDto dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        doctor.setSpecialization(dto.getSpecialization());
        doctor.setQualification(dto.getQualification());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setExperienceYears(dto.getExperienceYears());
        if (dto.getAvailableDays() != null) doctor.setAvailableDays(dto.getAvailableDays());
        if (dto.getAvailableFrom() != null) doctor.setAvailableFrom(dto.getAvailableFrom());
        if (dto.getAvailableTo() != null) doctor.setAvailableTo(dto.getAvailableTo());

        return mapToDto(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorDto createDoctor(DoctorDto dto) {
        if (userRepository.existsByUsername(dto.getFullName().toLowerCase().replace(" ", "."))) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        Long clinicId = resolveClinicId();
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));

        String username = dto.getFullName().toLowerCase().replace(" ", ".");
        String defaultPassword = "Doctor@123";

        User user = User.builder()
                .username(username)
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .role(Role.DOCTOR)
                .clinic(clinic)
                .status("ACTIVE")
                .build();
        user = userRepository.save(user);

        String doctorCode = "DOC-" + String.format("%03d", doctorRepository.count() + 1);

        Doctor doctor = Doctor.builder()
                .clinic(clinic)
                .user(user)
                .doctorCode(doctorCode)
                .specialization(dto.getSpecialization())
                .qualification(dto.getQualification())
                .licenseNumber(dto.getLicenseNumber())
                .consultationFee(dto.getConsultationFee())
                .experienceYears(dto.getExperienceYears() != null ? dto.getExperienceYears() : 0)
                .availableDays(dto.getAvailableDays() != null ? dto.getAvailableDays() : "MON,TUE,WED,THU,FRI")
                .availableFrom(dto.getAvailableFrom() != null ? dto.getAvailableFrom() : java.time.LocalTime.of(9, 0))
                .availableTo(dto.getAvailableTo() != null ? dto.getAvailableTo() : java.time.LocalTime.of(17, 0))
                .status("ACTIVE")
                .build();

        return mapToDto(doctorRepository.save(doctor));
    }

    public DoctorDto mapToDto(Doctor doc) {
        return DoctorDto.builder()
                .id(doc.getId())
                .clinicId(doc.getClinic().getId())
                .clinicName(doc.getClinic().getName())
                .userId(doc.getUser().getId())
                .fullName(doc.getUser().getFullName())
                .email(doc.getUser().getEmail())
                .phone(doc.getUser().getPhone())
                .doctorCode(doc.getDoctorCode())
                .specialization(doc.getSpecialization())
                .qualification(doc.getQualification())
                .licenseNumber(doc.getLicenseNumber())
                .consultationFee(doc.getConsultationFee())
                .experienceYears(doc.getExperienceYears())
                .availableDays(doc.getAvailableDays())
                .availableFrom(doc.getAvailableFrom())
                .availableTo(doc.getAvailableTo())
                .status(doc.getStatus())
                .build();
    }

    private Long resolveClinicId() {
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(Clinic::getId).orElse(1L);
    }
}
