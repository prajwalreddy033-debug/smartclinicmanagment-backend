package com.smartclinic.repository;

import com.smartclinic.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    Optional<Clinic> findByCode(String code);
    boolean existsByCode(String code);
}
