package com.hospital.management.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for Bill data transfer (request/response).
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BillDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long appointmentId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String billDate;
    private String dueDate;
}
