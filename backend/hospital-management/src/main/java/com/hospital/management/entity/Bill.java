package com.hospital.management.entity;

import com.hospital.management.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Bill entity — tracks billing and payment status for patient appointments.
 */
@Entity
@Table(name = "bills")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The patient this bill belongs to */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /** The appointment this bill is generated for */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @PositiveOrZero(message = "Amount must be zero or positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @PositiveOrZero(message = "Paid amount must be zero or positive")
    @Column(name = "paid_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "bill_date")
    @Builder.Default
    private LocalDate billDate = LocalDate.now();

    @Column(name = "due_date")
    private LocalDate dueDate;
}
