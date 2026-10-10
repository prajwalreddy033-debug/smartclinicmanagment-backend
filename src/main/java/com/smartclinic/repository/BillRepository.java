package com.smartclinic.repository;

import com.smartclinic.entity.Bill;
import com.smartclinic.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByInvoiceNumber(String invoiceNumber);
    Optional<Bill> findByAppointmentId(Long appointmentId);
    Page<Bill> findByClinicId(Long clinicId, Pageable pageable);
    List<Bill> findByPatientId(Long patientId);
    Page<Bill> findByPatientId(Long patientId, Pageable pageable);
    Page<Bill> findByClinicIdAndPaymentStatus(Long clinicId, PaymentStatus status, Pageable pageable);

    long countByClinicId(Long clinicId);

    @Query("SELECT COALESCE(SUM(b.netAmount), 0) FROM Bill b WHERE b.clinic.id = :clinicId")
    BigDecimal sumTotalBilled(@Param("clinicId") Long clinicId);

    @Query("SELECT COALESCE(SUM(b.paidAmount), 0) FROM Bill b WHERE b.clinic.id = :clinicId")
    BigDecimal sumTotalPaid(@Param("clinicId") Long clinicId);

    @Query("SELECT COALESCE(SUM(b.netAmount - b.paidAmount), 0) FROM Bill b WHERE b.clinic.id = :clinicId AND b.paymentStatus != 'PAID'")
    BigDecimal sumTotalOutstanding(@Param("clinicId") Long clinicId);
}
