package com.hospital.management.service;

import com.hospital.management.dto.RegisterRequest;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.Patient;
import com.hospital.management.entity.User;
import com.hospital.management.enums.Gender;
import com.hospital.management.enums.Role;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for user management — registration, lookup, authentication support.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user and create the corresponding role-specific profile.
     */
    @Transactional
    public User registerUser(RegisterRequest request) {
        logger.info("Registering new user: {} with role: {}", request.getUsername(), request.getRole());

        // Check for duplicates
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        // Build user entity
        Role role = Role.valueOf(request.getRole().toUpperCase());
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(role)
                .active(true)
                .build();

        user = userRepository.save(user);
        logger.info("User created with ID: {}", user.getId());

        // Create role-specific profile
        if (role == Role.DOCTOR) {
            Doctor doctor = Doctor.builder()
                    .user(user)
                    .specialization(request.getSpecialization() != null ? request.getSpecialization() : "General")
                    .qualification(request.getQualification())
                    .licenseNumber(request.getLicenseNumber())
                    .department(request.getDepartment())
                    .consultationFee(request.getConsultationFee() != null ?
                            BigDecimal.valueOf(request.getConsultationFee()) : BigDecimal.ZERO)
                    .build();
            doctorRepository.save(doctor);
            logger.info("Doctor profile created for user ID: {}", user.getId());
        } else if (role == Role.PATIENT) {
            Patient patient = Patient.builder()
                    .user(user)
                    .dateOfBirth(request.getDateOfBirth() != null ?
                            LocalDate.parse(request.getDateOfBirth()) : null)
                    .gender(request.getGender() != null ?
                            Gender.valueOf(request.getGender().toUpperCase()) : null)
                    .bloodGroup(request.getBloodGroup())
                    .address(request.getAddress())
                    .emergencyContact(request.getEmergencyContact())
                    .build();
            patientRepository.save(patient);
            logger.info("Patient profile created for user ID: {}", user.getId());
        }

        return user;
    }

    /** Find user by username */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /** Find user by ID */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /** Get all users */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Get users by role */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
