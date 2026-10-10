package com.smartclinic.dto.auth;

import com.smartclinic.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private Long clinicId;
    private String clinicName;
    private Long doctorId;
    private Long patientId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private String status;
    private LocalDateTime createdAt;
}
