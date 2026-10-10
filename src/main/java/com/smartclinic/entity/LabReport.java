package com.smartclinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(name = "report_number", nullable = false, unique = true, length = 50)
    private String reportNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "test_name", nullable = false, length = 150)
    private String testName;

    @Column(name = "test_category", length = 100)
    private String testCategory;

    @Column(name = "sample_date")
    private LocalDate sampleDate;

    @Column(name = "result_date")
    private LocalDate resultDate;

    @Column(name = "test_results", columnDefinition = "TEXT")
    private String testResults;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "COMPLETED";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
