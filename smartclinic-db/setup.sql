-- SmartClinic Database Setup Script
CREATE DATABASE IF NOT EXISTS smartclinic_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'clinic_user'@'%' IDENTIFIED BY 'clinic_password';
GRANT ALL PRIVILEGES ON smartclinic_db.* TO 'clinic_user'@'%';
FLUSH PRIVILEGES;

USE smartclinic_db;
