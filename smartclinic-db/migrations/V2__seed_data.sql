-- ==========================================================
-- SmartClinic Seed Data Migration: V2__seed_data.sql
-- ==========================================================

-- 1. Insert Clinics
INSERT INTO clinics (id, name, code, address, phone, email, tax_id, status) VALUES
(1, 'Metro Health Clinic', 'CLINIC-METRO', '100 Health Ave, Suite 300, New York, NY 10001', '+1-212-555-0100', 'contact@metroclinic.com', 'US-EIN-987654321', 'ACTIVE'),
(2, 'CarePoint Medical Center', 'CLINIC-CARE', '450 Wellness Blvd, Brooklyn, NY 11201', '+1-718-555-0150', 'care@carepoint.com', 'US-EIN-123456789', 'ACTIVE');

-- 2. Insert Users (BCrypt hash for "Admin@123" is set as standard $2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW or will be synchronized on app startup)
INSERT INTO users (id, clinic_id, username, email, password_hash, full_name, phone, role, status) VALUES
(1, NULL, 'superadmin', 'superadmin@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Alexander Wright', '+1-212-555-0001', 'SUPER_ADMIN', 'ACTIVE'),
(2, 1, 'admin.metro', 'admin.metro@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Eleanor Vance', '+1-212-555-0002', 'CLINIC_ADMIN', 'ACTIVE'),
(3, 2, 'admin.care', 'admin.care@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Marcus Sterling', '+1-718-555-0003', 'CLINIC_ADMIN', 'ACTIVE'),
(4, 1, 'dr.smith', 'dr.smith@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Dr. Robert Smith, MD', '+1-212-555-0111', 'DOCTOR', 'ACTIVE'),
(5, 1, 'dr.sarah', 'dr.sarah@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Dr. Sarah Jenkins, MD', '+1-212-555-0112', 'DOCTOR', 'ACTIVE'),
(6, 2, 'dr.alan', 'dr.alan@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Dr. Alan Parker, MD', '+1-718-555-0113', 'DOCTOR', 'ACTIVE'),
(7, 1, 'reception.metro', 'reception@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Emma Watson', '+1-212-555-0120', 'RECEPTIONIST', 'ACTIVE'),
(8, 2, 'reception.care', 'reception.care@smartclinic.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Lisa Ray', '+1-718-555-0121', 'RECEPTIONIST', 'ACTIVE'),
(9, 1, 'john.doe', 'john.doe@gmail.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'John Doe', '+1-212-555-0301', 'PATIENT', 'ACTIVE'),
(10, 1, 'emily.rose', 'emily.rose@gmail.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Emily Rose', '+1-212-555-0302', 'PATIENT', 'ACTIVE'),
(11, 2, 'michael.brown', 'michael.brown@gmail.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Michael Brown', '+1-718-555-0303', 'PATIENT', 'ACTIVE');

-- 3. Insert Doctors
INSERT INTO doctors (id, clinic_id, user_id, doctor_code, specialization, qualification, license_number, consultation_fee, experience_years, available_days, available_from, available_to, status) VALUES
(1, 1, 4, 'DOC-101', 'Cardiology', 'MD, FACC - Harvard Medical', 'NY-MED-849201', 80.00, 14, 'MON,TUE,WED,THU,FRI', '09:00:00', '17:00:00', 'ACTIVE'),
(2, 1, 5, 'DOC-102', 'Pediatrics', 'MD - Johns Hopkins', 'NY-MED-938210', 65.00, 9, 'MON,WED,FRI,SAT', '08:30:00', '16:00:00', 'ACTIVE'),
(3, 2, 6, 'DOC-201', 'Orthopedics', 'MD, MS (Ortho) - Columbia', 'NY-MED-192837', 90.00, 16, 'MON,TUE,THU,FRI', '09:30:00', '17:30:00', 'ACTIVE');

-- 4. Insert Patients
INSERT INTO patients (id, clinic_id, user_id, patient_code, first_name, last_name, gender, date_of_birth, blood_group, phone, email, address, emergency_contact_name, emergency_contact_phone, allergies, chronic_diseases, medical_history) VALUES
(1, 1, 9, 'PAT-1001', 'John', 'Doe', 'MALE', '1985-06-15', 'O_POSITIVE', '+1-212-555-0301', 'john.doe@gmail.com', '742 Evergreen Terrace, New York, NY', 'Mary Doe (Spouse)', '+1-212-555-0399', 'Penicillin, Peanuts', 'Hypertension', 'Appendectomy in 2018. Mild hypertension managed by Lisinopril.'),
(2, 1, 10, 'PAT-1002', 'Emily', 'Rose', 'FEMALE', '1992-11-20', 'A_POSITIVE', '+1-212-555-0302', 'emily.rose@gmail.com', '124 Conch Street, New York, NY', 'David Rose (Brother)', '+1-212-555-0388', 'Sulfa drugs', 'Asthma', 'Mild seasonal asthma, uses Albuterol inhaler when needed.'),
(3, 1, NULL, 'PAT-1003', 'William', 'Carter', 'MALE', '1970-03-08', 'B_POSITIVE', '+1-212-555-0304', 'w.carter@outlook.com', '88 Franklin Ave, New York, NY', 'Susan Carter (Wife)', '+1-212-555-0377', 'None known', 'Type 2 Diabetes', 'Type 2 diabetes diagnosed 2020. Annual eye checkups normal.'),
(4, 2, 11, 'PAT-2001', 'Michael', 'Brown', 'MALE', '1988-09-12', 'O_NEGATIVE', '+1-718-555-0303', 'michael.brown@gmail.com', '502 Atlantic Ave, Brooklyn, NY', 'Laura Brown', '+1-718-555-0366', 'Aspirin', 'None', 'Knee arthroscopy in 2022 following sports injury.');

-- 5. Insert Appointments
INSERT INTO appointments (id, clinic_id, appointment_number, patient_id, doctor_id, appointment_date, time_slot, reason_for_visit, status, token_number) VALUES
(1, 1, 'APT-2026-001', 1, 1, '2026-10-02', '09:30 AM - 10:00 AM', 'Routine cardiac evaluation & blood pressure check', 'COMPLETED', 1),
(2, 1, 'APT-2026-002', 2, 2, '2026-10-03', '10:00 AM - 10:30 AM', 'Pediatric wellness exam and vaccine review', 'CONFIRMED', 2),
(3, 1, 'APT-2026-003', 3, 1, '2026-10-04', '11:00 AM - 11:30 AM', 'Follow up on elevated blood glucose and chest discomfort', 'SCHEDULED', 3),
(4, 2, 'APT-2026-004', 4, 3, '2026-10-05', '02:00 PM - 02:30 PM', 'Right knee rehabilitation progress review', 'SCHEDULED', 1);

-- 6. Insert Consultations
INSERT INTO consultations (id, clinic_id, appointment_id, patient_id, doctor_id, symptoms, diagnosis, vital_bp, vital_pulse, vital_temp, vital_weight, vital_spo2, clinical_notes, treatment_plan) VALUES
(1, 1, 1, 1, 1, 'Mild fatigue after evening walk, occasional morning dizziness', 'Stage 1 Essential Hypertension (under moderate control)', '132/84', 72, '98.4 F', 78.50, 99, 'Patient demonstrates good medication adherence. Diet has improved with reduced sodium intake. Resting ECG shows normal sinus rhythm.', 'Continue Lisinopril 10mg daily. Moderate aerobic exercise 30 mins/day. Re-check BP in 2 weeks.');

-- 7. Insert Prescriptions
INSERT INTO prescriptions (id, clinic_id, prescription_number, consultation_id, patient_id, doctor_id, general_advice, diet_instructions, next_visit_date) VALUES
(1, 1, 'RX-2026-001', 1, 1, 1, 'Monitor blood pressure every morning and log readings in mobile app. Avoid heavy lifting.', 'Low sodium, DASH diet recommended. Drink at least 2.5 liters of water daily.', '2026-10-16');

-- 8. Insert Prescription Items
INSERT INTO prescription_items (id, prescription_id, medicine_name, dosage, frequency, duration, instructions) VALUES
(1, 1, 'Lisinopril', '10mg', '1-0-0 (Morning)', '30 days', 'Take once daily in the morning with a glass of water'),
(2, 1, 'Amlodipine', '5mg', '0-0-1 (Night)', '30 days', 'Take at bedtime after dinner'),
(3, 1, 'Omega-3 Fish Oil', '1000mg', '0-1-0 (Afternoon)', '60 days', 'Take with lunch');

-- 9. Insert Lab Reports
INSERT INTO lab_reports (id, clinic_id, report_number, patient_id, doctor_id, test_name, test_category, sample_date, result_date, test_results, file_url, status, notes) VALUES
(1, 1, 'LAB-2026-001', 1, 1, 'Comprehensive Lipid Profile', 'Biochemistry', '2026-10-01', '2026-10-02', 'Total Cholesterol: 185 mg/dL (Normal: <200)\nTriglycerides: 140 mg/dL (Normal: <150)\nHDL (Good): 52 mg/dL (Optimal: >40)\nLDL (Bad): 105 mg/dL (Desirable: <100)', '/uploads/lab-reports/LAB-2026-001.pdf', 'COMPLETED', 'Lipid levels in desirable range, well managed.'),
(2, 1, 'LAB-2026-002', 3, 1, 'HbA1c & Fasting Blood Sugar', 'Endocrinology', '2026-10-02', '2026-10-03', 'HbA1c: 6.8% (Target < 7.0%)\nFasting Glucose: 118 mg/dL', '/uploads/lab-reports/LAB-2026-002.pdf', 'COMPLETED', 'Glycemic control stable on current regimen.');

-- 10. Insert Bills
INSERT INTO bills (id, clinic_id, invoice_number, patient_id, appointment_id, total_amount, discount_amount, tax_amount, net_amount, paid_amount, payment_status, payment_method, payment_date, notes) VALUES
(1, 1, 'INV-2026-001', 1, 1, 140.00, 10.00, 10.40, 140.40, 140.40, 'PAID', 'CARD', '2026-10-02 10:15:00', 'Full payment received at front desk checkout via Visa.'),
(2, 1, 'INV-2026-002', 2, 2, 65.00, 0.00, 5.20, 70.20, 0.00, 'UNPAID', NULL, NULL, 'Pending check-in copay.');

-- 11. Insert Bill Items
INSERT INTO bill_items (id, bill_id, item_name, item_category, unit_price, quantity, total_price) VALUES
(1, 1, 'Cardiology Consultation', 'CONSULTATION', 80.00, 1, 80.00),
(2, 1, 'Comprehensive Lipid Panel', 'LAB_TEST', 60.00, 1, 60.00),
(3, 2, 'Pediatric Consultation', 'CONSULTATION', 65.00, 1, 65.00);

-- 12. Insert Follow Ups
INSERT INTO follow_ups (id, clinic_id, patient_id, doctor_id, appointment_id, follow_up_date, reminder_date, notes, status, reminder_sent) VALUES
(1, 1, 1, 1, 1, '2026-10-16', '2026-10-15', 'Cardiology follow-up for 2-week blood pressure adjustment', 'PENDING', FALSE),
(2, 2, 4, 3, 4, '2026-10-25', '2026-10-24', 'Orthopedic knee range of motion check', 'PENDING', FALSE);

-- 13. Insert Notifications
INSERT INTO notifications (id, clinic_id, user_id, title, message, type, is_read, created_at) VALUES
(1, 1, 9, 'Appointment Confirmed', 'Your appointment with Dr. Robert Smith is scheduled for Oct 2, 2026 at 09:30 AM.', 'APPOINTMENT', TRUE, '2026-10-01 14:00:00'),
(2, 1, 9, 'Prescription Ready', 'Dr. Robert Smith has issued your digital prescription RX-2026-001. You can view or download the PDF anytime.', 'PRESCRIPTION', FALSE, '2026-10-02 10:30:00'),
(3, 1, 9, 'Lab Report Available', 'Your Comprehensive Lipid Profile results (LAB-2026-001) are now ready.', 'LAB_REPORT', FALSE, '2026-10-02 11:00:00'),
(4, 1, 4, 'New Appointment Booked', 'Patient William Carter has booked an appointment for Oct 4, 2026 at 11:00 AM.', 'APPOINTMENT', FALSE, '2026-10-02 12:00:00');
