package com.smartclinic.config;

/**
 * ThreadLocal context holder for multi-clinic tenant isolation.
 * Automatically enforces data segregation across clinics.
 */
public class ClinicContextHolder {
    private static final ThreadLocal<Long> CURRENT_CLINIC_ID = new ThreadLocal<>();

    public static void setClinicId(Long clinicId) {
        CURRENT_CLINIC_ID.set(clinicId);
    }

    public static Long getClinicId() {
        return CURRENT_CLINIC_ID.get();
    }

    public static void clear() {
        CURRENT_CLINIC_ID.remove();
    }
}
