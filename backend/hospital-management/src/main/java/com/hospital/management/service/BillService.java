package com.hospital.management.service;

import com.hospital.management.dto.BillDTO;
import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.Bill;
import com.hospital.management.entity.Patient;
import com.hospital.management.enums.PaymentStatus;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.BillRepository;
import com.hospital.management.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService {

    private static final Logger logger = LoggerFactory.getLogger(BillService.class);

    @Autowired
    private BillRepository billRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<BillDTO> getAllBills() {
        return billRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BillDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
        return toDTO(bill);
    }

    public List<BillDTO> getBillsByPatientId(Long patientId) {
        return billRepository.findByPatientId(patientId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public BillDTO createBill(BillDTO dto) {
        logger.info("Creating bill for patient ID: {}", dto.getPatientId());
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", dto.getPatientId()));

        Bill bill = Bill.builder()
                .patient(patient)
                .amount(dto.getAmount())
                .paidAmount(dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(dto.getPaymentMethod())
                .billDate(LocalDate.now())
                .dueDate(dto.getDueDate() != null ? LocalDate.parse(dto.getDueDate()) : LocalDate.now().plusDays(30))
                .build();

        if (dto.getAppointmentId() != null) {
            Appointment appt = appointmentRepository.findById(dto.getAppointmentId()).orElse(null);
            bill.setAppointment(appt);
        }

        bill = billRepository.save(bill);
        logger.info("Bill created with ID: {}", bill.getId());
        return toDTO(bill);
    }

    @Transactional
    public BillDTO updatePayment(Long id, BigDecimal paidAmount, String paymentMethod) {
        logger.info("Updating payment for bill ID: {}", id);
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));

        bill.setPaidAmount(bill.getPaidAmount().add(paidAmount));
        if (bill.getPaidAmount().compareTo(bill.getAmount()) >= 0) {
            bill.setPaymentStatus(PaymentStatus.PAID);
            bill.setPaidAmount(bill.getAmount());
        } else {
            bill.setPaymentStatus(PaymentStatus.PARTIAL);
        }
        if (paymentMethod != null) bill.setPaymentMethod(paymentMethod);

        bill = billRepository.save(bill);
        return toDTO(bill);
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal total = billRepository.getTotalRevenue();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalPaidAmount() {
        BigDecimal total = billRepository.getTotalPaidAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    private BillDTO toDTO(Bill bill) {
        return BillDTO.builder()
                .id(bill.getId())
                .patientId(bill.getPatient().getId())
                .patientName(bill.getPatient().getUser().getFullName())
                .appointmentId(bill.getAppointment() != null ? bill.getAppointment().getId() : null)
                .amount(bill.getAmount())
                .paidAmount(bill.getPaidAmount())
                .paymentStatus(bill.getPaymentStatus().name())
                .paymentMethod(bill.getPaymentMethod())
                .billDate(bill.getBillDate() != null ? bill.getBillDate().toString() : null)
                .dueDate(bill.getDueDate() != null ? bill.getDueDate().toString() : null)
                .build();
    }
}
