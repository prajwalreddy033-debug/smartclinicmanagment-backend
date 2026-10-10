package com.smartclinic.repository;

import com.smartclinic.entity.Appointment;
import com.smartclinic.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    Page<Appointment> findByClinicId(Long clinicId, Pageable pageable);
    List<Appointment> findByClinicIdAndAppointmentDate(Long clinicId, LocalDate date);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.clinic.id = :clinicId " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:date IS NULL OR a.appointmentDate = :date) " +
           "AND (:doctorId IS NULL OR a.doctor.id = :doctorId)")
    Page<Appointment> filterAppointments(
            @Param("clinicId") Long clinicId,
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date,
            @Param("doctorId") Long doctorId,
            Pageable pageable
    );

    long countByClinicId(Long clinicId);
    long countByClinicIdAndStatus(Long clinicId, AppointmentStatus status);
    long countByClinicIdAndAppointmentDate(Long clinicId, LocalDate date);
}
