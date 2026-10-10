package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.patient.PatientCreateDto;
import com.smartclinic.dto.patient.PatientResponseDto;
import com.smartclinic.entity.Clinic;
import com.smartclinic.entity.Patient;
import com.smartclinic.repository.ClinicRepository;
import com.smartclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ClinicRepository clinicRepository;

    @Transactional(readOnly = true)
    public PagedResponse<PatientResponseDto> getPatients(String search, Pageable pageable) {
        Long clinicId = resolveClinicId(null);
        Page<Patient> page;

        if (search != null && !search.trim().isEmpty()) {
            page = patientRepository.searchPatients(clinicId, search.trim(), pageable);
        } else {
            page = patientRepository.findByClinicId(clinicId, pageable);
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
    public PatientResponseDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        verifyClinicAccess(patient.getClinic().getId());
        return mapToDto(patient);
    }

    @Transactional
    public PatientResponseDto registerPatient(PatientCreateDto dto) {
        Long clinicId = resolveClinicId(dto.getClinicId());
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        String code = "PAT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Patient patient = Patient.builder()
                .clinic(clinic)
                .patientCode(code)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .bloodGroup(dto.getBloodGroup())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .emergencyContactName(dto.getEmergencyContactName())
                .emergencyContactPhone(dto.getEmergencyContactPhone())
                .allergies(dto.getAllergies())
                .chronicDiseases(dto.getChronicDiseases())
                .medicalHistory(dto.getMedicalHistory())
                .build();

        return mapToDto(patientRepository.save(patient));
    }

    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientCreateDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        verifyClinicAccess(patient.getClinic().getId());

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setGender(dto.getGender());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        patient.setAllergies(dto.getAllergies());
        patient.setChronicDiseases(dto.getChronicDiseases());
        patient.setMedicalHistory(dto.getMedicalHistory());

        return mapToDto(patientRepository.save(patient));
    }

    public PatientResponseDto mapToDto(Patient p) {
        Integer age = null;
        if (p.getDateOfBirth() != null) {
            age = Period.between(p.getDateOfBirth(), LocalDate.now()).getYears();
        }

        return PatientResponseDto.builder()
                .id(p.getId())
                .clinicId(p.getClinic().getId())
                .clinicName(p.getClinic().getName())
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .patientCode(p.getPatientCode())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .fullName(p.getFullName())
                .gender(p.getGender())
                .dateOfBirth(p.getDateOfBirth())
                .age(age)
                .bloodGroup(p.getBloodGroup())
                .phone(p.getPhone())
                .email(p.getEmail())
                .address(p.getAddress())
                .emergencyContactName(p.getEmergencyContactName())
                .emergencyContactPhone(p.getEmergencyContactPhone())
                .allergies(p.getAllergies())
                .chronicDiseases(p.getChronicDiseases())
                .medicalHistory(p.getMedicalHistory())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private Long resolveClinicId(Long explicitClinicId) {
        if (explicitClinicId != null) return explicitClinicId;
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(Clinic::getId).orElse(1L);
    }

    private void verifyClinicAccess(Long entityClinicId) {
        Long contextClinicId = ClinicContextHolder.getClinicId();
        if (contextClinicId != null && !contextClinicId.equals(entityClinicId)) {
            throw new BadRequestException("Cross-clinic data access is not permitted");
        }
    }
}
