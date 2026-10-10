package com.smartclinic.repository;

import com.smartclinic.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByDoctorCode(String doctorCode);
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByClinicId(Long clinicId);
    Page<Doctor> findByClinicId(Long clinicId, Pageable pageable);
    List<Doctor> findByClinicIdAndSpecialization(Long clinicId, String specialization);
    long countByClinicId(Long clinicId);
}
