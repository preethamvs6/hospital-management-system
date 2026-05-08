package com.hospital.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for user registration requests.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @NotBlank(message = "Role is required")
    private String role; // ADMIN, DOCTOR, PATIENT

    // Doctor-specific fields (optional, required if role=DOCTOR)
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private String department;
    private Double consultationFee;

    // Patient-specific fields (optional, required if role=PATIENT)
    private String dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String emergencyContact;
}
