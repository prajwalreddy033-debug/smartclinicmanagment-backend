package com.smartclinic.dto.auth;

import com.smartclinic.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Long clinicId;
    private String clinicName;
    private Long doctorId; // populated if role is DOCTOR
    private Long patientId; // populated if role is PATIENT
}
