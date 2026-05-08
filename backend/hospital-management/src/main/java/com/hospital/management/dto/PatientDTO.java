package com.hospital.management.dto;

import lombok.*;

/**
 * DTO for Patient data transfer (request/response).
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PatientDTO {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String emergencyContact;
    private String medicalHistory;
    private Boolean active;
}
