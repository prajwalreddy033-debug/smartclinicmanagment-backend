package com.smartclinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "doctor_code", nullable = false, unique = true, length = 50)
    private String doctorCode;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(nullable = false, length = 100)
    private String qualification;

    @Column(name = "license_number", nullable = false, length = 80)
    private String licenseNumber;

    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal consultationFee = new BigDecimal("50.00");

    @Column(name = "experience_years")
    @Builder.Default
    private Integer experienceYears = 0;

    @Column(name = "available_days", length = 100)
    @Builder.Default
    private String availableDays = "MON,TUE,WED,THU,FRI";

    @Column(name = "available_from")
    @Builder.Default
    private LocalTime availableFrom = LocalTime.of(9, 0);

    @Column(name = "available_to")
    @Builder.Default
    private LocalTime availableTo = LocalTime.of(17, 0);

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
