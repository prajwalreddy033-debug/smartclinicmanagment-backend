package com.smartclinic.config;

import com.smartclinic.entity.*;
import com.smartclinic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabReportRepository labReportRepository;
    private final BillRepository billRepository;
    private final FollowUpRepository followUpRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Create upload folders
        File uploadFolder = new File("./uploads/lab-reports");
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String defaultPassword = passwordEncoder.encode("Admin@123");

        if (userRepository.count() > 0) {
            log.info("Database contains {} users. Synchronizing demo credentials...", userRepository.count());
            for (User u : userRepository.findAll()) {
                u.setPasswordHash(defaultPassword);
                userRepository.save(u);
            }
        } else {
            log.info("Database is empty. Initializing baseline clinic and admin...");
            Clinic newClinic = clinicRepository.save(Clinic.builder()
                    .name("Smart Clinic")
                    .code("CLINIC-001")
                    .address("123 Health Street, Medical District")
                    .phone("+1-555-0100")
                    .email("contact@smartclinic.com")
                    .taxId("TAX-001")
                    .status("ACTIVE")
                    .build());

            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@smartclinic.com")
                    .passwordHash(defaultPassword)
                    .fullName("Clinic Admin")
                    .phone("+1-555-0101")
                    .role(Role.CLINIC_ADMIN)
                    .clinic(newClinic)
                    .status("ACTIVE")
                    .build());
        }

        // Ensure admin@smartclinic.com exists alongside admin.metro
        if (!userRepository.existsByEmail("admin@smartclinic.com")) {
            Clinic clinic = clinicRepository.findAll().stream().findFirst().orElse(null);
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@smartclinic.com")
                    .passwordHash(defaultPassword)
                    .fullName("Clinic Admin")
                    .phone("+1-555-0101")
                    .role(Role.CLINIC_ADMIN)
                    .clinic(clinic)
                    .status("ACTIVE")
                    .build());
            log.info("Created admin@smartclinic.com");
        }

        log.info("SmartClinic data initialization completed successfully!");
    }
}
