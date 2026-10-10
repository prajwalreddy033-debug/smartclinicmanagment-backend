# SmartClinic - Backend Service

Production-ready backend for the **Smart Clinic Management System**, built with **Spring Boot 3**, **Java 21**, and **MySQL 8**.

---

## Features
- **Multi-Role Authentication**: JWT-based authentication supporting Clinic Admin, Doctor, Receptionist, and Patient roles.
- **Appointment Management**: Complete scheduling, rescheduling, time slots, and status tracking.
- **Consultations & Electronic Prescriptions**: Doctor consultations, diagnosis, vitals, prescriptions, and PDF generation.
- **Billing & Invoices**: Auto-invoicing, payments, tax/discount calculation, and PDF exports.
- **Database Migrations**: Automatic Flyway migrations (`V1__init_schema.sql` and `V2__seed_data.sql`).
- **Cloud Database Ready**: Seamlessly configurable for local MySQL or **Aiven Cloud MySQL** via `.env`.
- **API Documentation**: Interactive Swagger / OpenAPI 3 UI.

---

## Tech Stack
- **Framework**: Spring Boot 3.2.4
- **Language**: Java 21
- **Database**: MySQL 8.x / Flyway
- **Security**: Spring Security 6 + JSON Web Tokens (JJWT)
- **PDF Generation**: OpenPDF
- **Docs**: SpringDoc OpenAPI (Swagger UI)

---

## Getting Started

### 1. Environment Setup
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

Configure your database in `.env`:
```env
# Local MySQL:
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/smartclinic_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=clinic_user
DB_PASSWORD=clinic_password

# Or Aiven Cloud MySQL:
# SPRING_DATASOURCE_URL=jdbc:mysql://<HOST>:<PORT>/defaultdb?sslMode=REQUIRED&serverTimezone=UTC
# DB_USERNAME=avnadmin
# DB_PASSWORD=<AIVEN_PASSWORD>
```

### 2. Build and Run
```bash
mvn clean compile
mvn spring-boot:run
```

- **Backend API**: `http://localhost:8070`
- **Swagger Documentation**: `http://localhost:8070/swagger-ui.html`
- **OpenAPI Schema**: `http://localhost:8070/v3/api-docs`

---

## Repository Structure
```text
backend/
├── src/main/java/com/smartclinic/   # Controllers, Services, Repositories, Entities
├── src/main/resources/              # Configuration & Flyway migrations
├── smartclinic-db/                  # Local Docker Compose & database setup
├── docs/                            # Test cases and defect reports
├── .env.example                     # Environment template
└── pom.xml                          # Maven build configuration
```
