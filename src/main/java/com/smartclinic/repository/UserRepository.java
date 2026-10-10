package com.smartclinic.repository;

import com.smartclinic.entity.Role;
import com.smartclinic.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<User> findByClinicId(Long clinicId);
    Page<User> findByClinicId(Long clinicId, Pageable pageable);
    List<User> findByClinicIdAndRole(Long clinicId, Role role);
    long countByClinicId(Long clinicId);
}
