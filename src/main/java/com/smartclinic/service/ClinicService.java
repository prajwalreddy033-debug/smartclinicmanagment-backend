package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.dto.clinic.ClinicDto;
import com.smartclinic.entity.Clinic;
import com.smartclinic.repository.ClinicRepository;
import com.smartclinic.repository.DoctorRepository;
import com.smartclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public List<ClinicDto> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClinicDto getClinicById(Long id) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));
        return mapToDto(clinic);
    }

    @Transactional
    public ClinicDto createClinic(ClinicDto dto) {
        if (clinicRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Clinic code already exists: " + dto.getCode());
        }

        Clinic clinic = Clinic.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase().trim())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .taxId(dto.getTaxId())
                .status("ACTIVE")
                .build();

        return mapToDto(clinicRepository.save(clinic));
    }

    @Transactional
    public ClinicDto updateClinic(Long id, ClinicDto dto) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));

        clinic.setName(dto.getName());
        clinic.setAddress(dto.getAddress());
        clinic.setPhone(dto.getPhone());
        clinic.setEmail(dto.getEmail());
        clinic.setTaxId(dto.getTaxId());
        if (dto.getStatus() != null) {
            clinic.setStatus(dto.getStatus());
        }

        return mapToDto(clinicRepository.save(clinic));
    }

    private ClinicDto mapToDto(Clinic clinic) {
        long doctors = doctorRepository.countByClinicId(clinic.getId());
        long patients = patientRepository.countByClinicId(clinic.getId());

        return ClinicDto.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .code(clinic.getCode())
                .address(clinic.getAddress())
                .phone(clinic.getPhone())
                .email(clinic.getEmail())
                .taxId(clinic.getTaxId())
                .status(clinic.getStatus())
                .createdAt(clinic.getCreatedAt())
                .totalDoctors(doctors)
                .totalPatients(patients)
                .build();
    }
}
