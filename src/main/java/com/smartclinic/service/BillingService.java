package com.smartclinic.service;

import com.smartclinic.common.BadRequestException;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.common.ResourceNotFoundException;
import com.smartclinic.config.ClinicContextHolder;
import com.smartclinic.dto.billing.*;
import com.smartclinic.entity.*;
import com.smartclinic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PagedResponse<BillResponseDto> getBills(Long patientId, PaymentStatus status, Pageable pageable) {
        Page<Bill> page;
        if (patientId != null) {
            page = billRepository.findByPatientId(patientId, pageable);
        } else if (status != null) {
            Long clinicId = resolveClinicId(null);
            page = billRepository.findByClinicIdAndPaymentStatus(clinicId, status, pageable);
        } else {
            Long clinicId = resolveClinicId(null);
            page = billRepository.findByClinicId(clinicId, pageable);
        }

        return new PagedResponse<>(
                page.getContent().stream().map(this::mapToDto).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public BillResponseDto getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return mapToDto(bill);
    }

    @Transactional
    public BillResponseDto createBill(BillCreateDto dto) {
        Long clinicId = resolveClinicId(dto.getClinicId());
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found: " + clinicId));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + dto.getPatientId()));

        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(dto.getAppointmentId()).orElse(null);
        }

        String invoiceNumber = "INV-" + LocalDate.now().getYear() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BigDecimal subtotal = BigDecimal.ZERO;
        Bill bill = Bill.builder()
                .clinic(clinic)
                .invoiceNumber(invoiceNumber)
                .patient(patient)
                .appointment(appointment)
                .notes(dto.getNotes())
                .paymentStatus(PaymentStatus.UNPAID)
                .paidAmount(BigDecimal.ZERO)
                .build();

        for (BillItemDto itemDto : dto.getItems()) {
            BigDecimal lineTotal = itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            BillItem item = BillItem.builder()
                    .itemName(itemDto.getItemName())
                    .itemCategory(itemDto.getItemCategory() != null ? itemDto.getItemCategory() : "CONSULTATION")
                    .unitPrice(itemDto.getUnitPrice())
                    .quantity(itemDto.getQuantity())
                    .totalPrice(lineTotal)
                    .build();
            bill.addItem(item);
        }

        BigDecimal discount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal tax = dto.getTaxAmount() != null ? dto.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal netAmount = subtotal.subtract(discount).add(tax);

        bill.setTotalAmount(subtotal);
        bill.setDiscountAmount(discount);
        bill.setTaxAmount(tax);
        bill.setNetAmount(netAmount.max(BigDecimal.ZERO));

        bill = billRepository.save(bill);

        // Notify patient
        if (patient.getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(clinic)
                    .user(patient.getUser())
                    .title("New Invoice Generated")
                    .message("Invoice " + invoiceNumber + " for $" + bill.getNetAmount() + " has been generated.")
                    .type("BILLING")
                    .build());
        }

        return mapToDto(bill);
    }

    @Transactional
    public BillResponseDto recordPayment(Long billId, BillPaymentDto dto) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + billId));

        if (bill.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Invoice is already fully paid");
        }

        BigDecimal newPaidAmount = bill.getPaidAmount().add(dto.getAmount());
        bill.setPaidAmount(newPaidAmount);
        bill.setPaymentMethod(dto.getPaymentMethod());
        bill.setPaymentDate(LocalDateTime.now());

        if (newPaidAmount.compareTo(bill.getNetAmount()) >= 0) {
            bill.setPaymentStatus(PaymentStatus.PAID);
        } else {
            bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        }

        if (dto.getNotes() != null) {
            bill.setNotes((bill.getNotes() != null ? bill.getNotes() + "\n" : "") + dto.getNotes());
        }

        bill = billRepository.save(bill);

        // Notify patient
        if (bill.getPatient().getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .clinic(bill.getClinic())
                    .user(bill.getPatient().getUser())
                    .title("Payment Received")
                    .message("Payment of $" + dto.getAmount() + " received for invoice " + bill.getInvoiceNumber() +
                            ". Status: " + bill.getPaymentStatus())
                    .type("BILLING")
                    .build());
        }

        return mapToDto(bill);
    }

    public BillResponseDto mapToDto(Bill b) {
        BigDecimal due = b.getNetAmount().subtract(b.getPaidAmount()).max(BigDecimal.ZERO);

        return BillResponseDto.builder()
                .id(b.getId())
                .clinicId(b.getClinic().getId())
                .clinicName(b.getClinic().getName())
                .invoiceNumber(b.getInvoiceNumber())
                .patientId(b.getPatient().getId())
                .patientCode(b.getPatient().getPatientCode())
                .patientName(b.getPatient().getFullName())
                .patientPhone(b.getPatient().getPhone())
                .appointmentId(b.getAppointment() != null ? b.getAppointment().getId() : null)
                .totalAmount(b.getTotalAmount())
                .discountAmount(b.getDiscountAmount())
                .taxAmount(b.getTaxAmount())
                .netAmount(b.getNetAmount())
                .paidAmount(b.getPaidAmount())
                .dueAmount(due)
                .paymentStatus(b.getPaymentStatus())
                .paymentMethod(b.getPaymentMethod())
                .paymentDate(b.getPaymentDate())
                .notes(b.getNotes())
                .items(b.getItems().stream().map(i -> BillItemDto.builder()
                        .id(i.getId())
                        .itemName(i.getItemName())
                        .itemCategory(i.getItemCategory())
                        .unitPrice(i.getUnitPrice())
                        .quantity(i.getQuantity())
                        .totalPrice(i.getTotalPrice())
                        .build()).collect(Collectors.toList()))
                .createdAt(b.getCreatedAt())
                .build();
    }

    private Long resolveClinicId(Long explicitClinicId) {
        if (explicitClinicId != null) return explicitClinicId;
        Long contextId = ClinicContextHolder.getClinicId();
        if (contextId != null) return contextId;
        return clinicRepository.findAll().stream().findFirst().map(Clinic::getId).orElse(1L);
    }
}
