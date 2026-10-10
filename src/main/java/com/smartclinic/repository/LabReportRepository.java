package com.smartclinic.repository;

import com.smartclinic.entity.LabReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabReportRepository extends JpaRepository<LabReport, Long> {
    Optional<LabReport> findByReportNumber(String reportNumber);
    Page<LabReport> findByClinicId(Long clinicId, Pageable pageable);
    List<LabReport> findByPatientId(Long patientId);
    Page<LabReport> findByPatientId(Long patientId, Pageable pageable);
    long countByClinicId(Long clinicId);
}
