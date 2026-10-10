package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.common.UnauthorizedException;
import com.smartclinic.config.JwtTokenProvider;
import com.smartclinic.dto.auth.AuthResponse;
import com.smartclinic.dto.auth.LoginRequest;
import com.smartclinic.dto.auth.RegisterRequest;
import com.smartclinic.dto.auth.UserSummaryDto;
import com.smartclinic.entity.Clinic;
import com.smartclinic.entity.Doctor;
import com.smartclinic.entity.Patient;
import com.smartclinic.entity.Role;
import com.smartclinic.entity.User;
import com.smartclinic.repository.ClinicRepository;
import com.smartclinic.repository.DoctorRepository;
import com.smartclinic.repository.PatientRepository;
import com.smartclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getUsername().trim();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UnauthorizedException("Invalid username/email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username/email or password");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Account is inactive or disabled");
        }

        Long clinicId = user.getClinic() != null ? user.getClinic().getId() : null;
        String clinicName = user.getClinic() != null ? user.getClinic().getName() : "All Clinics";

        Long doctorId = null;
        if (user.getRole() == Role.DOCTOR) {
            Optional<Doctor> doc = doctorRepository.findByUserId(user.getId());
            if (doc.isPresent()) {
                doctorId = doc.get().getId();
            }
        }

        Long patientId = null;
        if (user.getRole() == Role.PATIENT) {
            Optional<Patient> pat = patientRepository.findByUserId(user.getId());
            if (pat.isPresent()) {
                patientId = pat.get().getId();
            }
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getId(), user.getRole(), clinicId, user.getFullName());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .clinicId(clinicId)
                .clinicName(clinicName)
                .doctorId(doctorId)
                .patientId(patientId)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        // Public registration is intentionally patient-only and uses the clinic selected by the system.
        Clinic clinic = clinicRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No clinic is configured for registration"));

        Role role = Role.PATIENT;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(role)
                .clinic(clinic)
                .status("ACTIVE")
                .build();

        user = userRepository.save(user);

        // If registered as patient, create baseline patient profile
        Long patientId = null;
        if (role == Role.PATIENT && clinic != null) {
            String[] names = request.getFullName().split(" ", 2);
            String firstName = names[0];
            String lastName = names.length > 1 ? names[1] : "User";

            Patient patient = Patient.builder()
                    .clinic(clinic)
                    .user(user)
                    .patientCode("PAT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .firstName(firstName)
                    .lastName(lastName)
                    .gender(request.getGender() != null && !request.getGender().isBlank() ? request.getGender().trim().toUpperCase() : "OTHER")
                    .dateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth() : java.time.LocalDate.of(2000, 1, 1))
                    .phone(request.getPhone() != null ? request.getPhone() : "+1-000-000-0000")
                    .email(request.getEmail())
                    .build();
            patient = patientRepository.save(patient);
            patientId = patient.getId();
        }

        Long doctorId = null;

        Long clinicId = clinic != null ? clinic.getId() : null;
        String clinicName = clinic != null ? clinic.getName() : null;
        String token = tokenProvider.generateToken(user.getUsername(), user.getId(), user.getRole(), clinicId, user.getFullName());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .clinicId(clinicId)
                .clinicName(clinicName)
                .doctorId(doctorId)
                .patientId(patientId)
                .build();
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Long doctorId = null;
        if (user.getRole() == Role.DOCTOR) {
            var doc = doctorRepository.findByUserId(user.getId());
            if (doc.isPresent()) doctorId = doc.get().getId();
        }

        Long patientId = null;
        if (user.getRole() == Role.PATIENT) {
            var pat = patientRepository.findByUserId(user.getId());
            if (pat.isPresent()) patientId = pat.get().getId();
        }

        return UserSummaryDto.builder()
                .id(user.getId())
                .clinicId(user.getClinic() != null ? user.getClinic().getId() : null)
                .clinicName(user.getClinic() != null ? user.getClinic().getName() : "All Clinics")
                .doctorId(doctorId)
                .patientId(patientId)
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
