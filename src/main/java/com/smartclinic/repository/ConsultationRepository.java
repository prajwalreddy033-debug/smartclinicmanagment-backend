package com.smartclinic.repository;

import com.smartclinic.entity.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByAppointmentId(Long appointmentId);
    Page<Consultation> findByClinicId(Long clinicId, Pageable pageable);
    List<Consultation> findByPatientId(Long patientId);
    Page<Consultation> findByPatientId(Long patientId, Pageable pageable);
    Page<Consultation> findByDoctorId(Long doctorId, Pageable pageable);
    long countByClinicId(Long clinicId);
}
