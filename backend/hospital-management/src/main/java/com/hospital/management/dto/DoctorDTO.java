package com.hospital.management.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for Doctor data transfer (request/response).
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DoctorDTO {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private BigDecimal consultationFee;
    private String availableDays;
    private String availableTime;
    private String department;
    private Boolean active;
}
