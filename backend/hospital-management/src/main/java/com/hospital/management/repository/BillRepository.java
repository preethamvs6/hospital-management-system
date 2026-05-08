package com.hospital.management.repository;

import com.hospital.management.entity.Bill;
import com.hospital.management.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Bill entity — supports payment status filtering and revenue queries.
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByPatientId(Long patientId);

    List<Bill> findByPaymentStatus(PaymentStatus paymentStatus);

    Optional<Bill> findByAppointmentId(Long appointmentId);

    @Query("SELECT SUM(b.amount) FROM Bill b")
    BigDecimal getTotalRevenue();

    @Query("SELECT SUM(b.paidAmount) FROM Bill b")
    BigDecimal getTotalPaidAmount();

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.paymentStatus = :status")
    long countByPaymentStatus(@Param("status") PaymentStatus status);
}
