package com.smartclinic.repository;

import com.smartclinic.entity.FollowUp;
import com.smartclinic.entity.FollowUpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    Page<FollowUp> findByClinicId(Long clinicId, Pageable pageable);
    List<FollowUp> findByClinicIdAndFollowUpDate(Long clinicId, LocalDate date);
    Page<FollowUp> findByPatientId(Long patientId, Pageable pageable);
    Page<FollowUp> findByDoctorId(Long doctorId, Pageable pageable);
    Page<FollowUp> findByClinicIdAndStatus(Long clinicId, FollowUpStatus status, Pageable pageable);
    long countByClinicId(Long clinicId);
}
