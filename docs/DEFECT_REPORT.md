# SmartClinic – Defect Fix Report

| ID | Module | Defect | Severity | Fix |
|---|---|---|---|---|
| SC-001 | Registration | Public user could request privileged roles | Critical | Public registration is PATIENT-only |
| SC-002 | Multi-clinic security | Client could send `X-Clinic-ID` to switch clinic context | Critical | Clinic comes only from signed JWT |
| SC-003 | Prescriptions | Patient/doctor ownership was not enforced on prescription reads/PDFs | Critical | Server-side ownership and clinic authorization |
| SC-004 | Consultations | Consultation reads were filter-based instead of identity-based | High | Patient/doctor queries are forced to their own records |
| SC-005 | Prescriptions | Any allowed staff/doctor could potentially prescribe another doctor's consultation | High | Only the treating doctor can issue the prescription |
| SC-006 | Billing | Frontend called `/payment`, backend exposed `/payments` | High | Frontend endpoint corrected |
| SC-007 | Database | Main configuration defaulted to in-memory H2 | High | Main configuration now uses MySQL + Flyway |
| SC-008 | Database startup | Docker mounted migrations while Flyway was also enabled | High | Docker only starts MySQL; Flyway owns migrations |
| SC-009 | Security | JWT secret was hard-coded | High | Environment variable with development fallback |
| SC-010 | Security | CORS allowed every origin with credentials | High | Local frontend origins are explicitly allowed |
| SC-011 | Security | Disabled users could keep using an old JWT | High | JWT filter re-checks active account status |
| SC-012 | Data integrity | Public identifiers used `count()+1` | Medium | UUID-backed identifiers for new records |
| SC-013 | UI | Non-functional New Consultation/Create Invoice buttons | Medium | Replaced with accurate workflow guidance |
| SC-014 | Registration | New patients received fake default demographic data | Medium | Registration now captures date of birth and gender |

## Prescription acceptance criteria

- Doctor can select only their own completed consultations.
- Doctor can add one or more medicines with dosage, frequency, duration and instructions.
- Prescription is stored against the consultation, doctor and patient.
- Patient receives a prescription notification.
- Patient sees only their own prescriptions.
- Patient can download the prescription as PDF.
- Another patient cannot fetch or download the prescription by changing its ID.
