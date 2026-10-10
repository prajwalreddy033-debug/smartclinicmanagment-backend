# SmartClinic – Key Test Cases

| ID | Test | Expected result |
|---|---|---|
| TC-01 | Register a new account | Account is created as PATIENT only |
| TC-02 | Send `role=CLINIC_ADMIN` to public registration | Server still creates PATIENT |
| TC-03 | Login with demo doctor | Doctor dashboard opens |
| TC-04 | Doctor opens Prescriptions | Only that doctor's prescriptions are listed |
| TC-05 | Doctor creates prescription for own consultation | Prescription is saved successfully |
| TC-06 | Doctor creates prescription for another doctor's consultation | Request is rejected |
| TC-07 | Patient logs in | Patient sees only own prescriptions |
| TC-08 | Patient opens prescription | Medicines, dosage, frequency, duration and advice are visible |
| TC-09 | Patient downloads PDF | Browser downloads prescription PDF |
| TC-10 | Patient requests another patient's prescription ID | Server returns not found/unauthorized |
| TC-11 | Disabled account uses an old token | Request is rejected |
| TC-12 | Frontend processes a bill payment | Request reaches `/bills/{id}/payments` |
| TC-13 | Start MySQL with Docker and backend | Flyway creates/validates schema and application starts |
| TC-14 | Send `X-Clinic-ID` header | Header cannot change signed JWT clinic context |
