-- ==========================================================
-- SmartClinic Schema Migration: V1__init_schema.sql
-- ==========================================================

-- 1. Clinics Table
CREATE TABLE IF NOT EXISTS clinics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100),
    tax_id VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE SET NULL,
    INDEX idx_users_role (role),
    INDEX idx_users_clinic (clinic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    patient_code VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    blood_group VARCHAR(10),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(120),
    address VARCHAR(255),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    allergies TEXT,
    chronic_diseases TEXT,
    medical_history TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_patients_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    INDEX idx_patients_clinic (clinic_id),
    INDEX idx_patients_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL UNIQUE,
    doctor_code VARCHAR(50) NOT NULL UNIQUE,
    specialization VARCHAR(100) NOT NULL,
    qualification VARCHAR(100) NOT NULL,
    license_number VARCHAR(80) NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 50.00,
    experience_years INT DEFAULT 0,
    available_days VARCHAR(100) DEFAULT 'MON,TUE,WED,THU,FRI',
    available_from TIME DEFAULT '09:00:00',
    available_to TIME DEFAULT '17:00:00',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_doctors_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_doctors_clinic (clinic_id),
    INDEX idx_doctors_spec (specialization)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    appointment_number VARCHAR(50) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot VARCHAR(30) NOT NULL,
    reason_for_visit TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    cancellation_reason TEXT,
    token_number INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,
    INDEX idx_appointments_clinic (clinic_id),
    INDEX idx_appointments_date (appointment_date),
    INDEX idx_appointments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Consultations Table
CREATE TABLE IF NOT EXISTS consultations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    appointment_id BIGINT NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    symptoms TEXT NOT NULL,
    diagnosis TEXT NOT NULL,
    vital_bp VARCHAR(20),
    vital_pulse INT,
    vital_temp VARCHAR(20),
    vital_weight DECIMAL(5, 2),
    vital_spo2 INT,
    clinical_notes TEXT,
    treatment_plan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_consultations_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_consultations_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id) ON DELETE CASCADE,
    CONSTRAINT fk_consultations_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_consultations_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,
    INDEX idx_consultations_clinic (clinic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    prescription_number VARCHAR(50) NOT NULL UNIQUE,
    consultation_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    general_advice TEXT,
    diet_instructions TEXT,
    next_visit_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prescriptions_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_prescriptions_consultation FOREIGN KEY (consultation_id) REFERENCES consultations (id) ON DELETE CASCADE,
    CONSTRAINT fk_prescriptions_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_prescriptions_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,
    INDEX idx_prescriptions_clinic (clinic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Prescription Items Table
CREATE TABLE IF NOT EXISTS prescription_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_name VARCHAR(150) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    instructions VARCHAR(255),
    CONSTRAINT fk_prescription_items_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions (id) ON DELETE CASCADE,
    INDEX idx_prescription_items_p (prescription_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Lab Reports Table
CREATE TABLE IF NOT EXISTS lab_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    report_number VARCHAR(50) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NULL,
    test_name VARCHAR(150) NOT NULL,
    test_category VARCHAR(100),
    sample_date DATE,
    result_date DATE,
    test_results TEXT,
    file_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lab_reports_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_reports_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_reports_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE SET NULL,
    INDEX idx_lab_reports_clinic (clinic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bills Table
CREATE TABLE IF NOT EXISTS bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT NULL,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    net_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(10, 2) DEFAULT 0.00,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
    payment_method VARCHAR(50),
    payment_date DATETIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bills_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_bills_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_bills_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id) ON DELETE SET NULL,
    INDEX idx_bills_clinic (clinic_id),
    INDEX idx_bills_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Bill Items Table
CREATE TABLE IF NOT EXISTS bill_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    item_category VARCHAR(50) NOT NULL DEFAULT 'CONSULTATION',
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_bill_items_bill FOREIGN KEY (bill_id) REFERENCES bills (id) ON DELETE CASCADE,
    INDEX idx_bill_items_bill (bill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Follow Ups Table
CREATE TABLE IF NOT EXISTS follow_ups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT NULL,
    follow_up_date DATE NOT NULL,
    reminder_date DATE,
    notes TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    reminder_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_follow_ups_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_follow_ups_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_follow_ups_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,
    CONSTRAINT fk_follow_ups_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id) ON DELETE SET NULL,
    INDEX idx_follow_ups_clinic (clinic_id),
    INDEX idx_follow_ups_date (follow_up_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_id BIGINT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
