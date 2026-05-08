package com.hospital.management.service;

import com.hospital.management.dto.PatientDTO;
import com.hospital.management.entity.Patient;
import com.hospital.management.enums.Gender;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for patient management — CRUD operations, search, and DTO conversion.
 */
@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    private PatientRepository patientRepository;

    /** Get all patients */
    public List<PatientDTO> getAllPatients() {
        logger.info("Fetching all patients");
        return patientRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Get patient by ID */
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        return toDTO(patient);
    }

    /** Get patient by user ID */
    public PatientDTO getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "userId", userId));
        return toDTO(patient);
    }

    /** Get patient entity by user ID (internal use) */
    public Patient getPatientEntityByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "userId", userId));
    }

    /** Update patient profile */
    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        logger.info("Updating patient ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        if (dto.getGender() != null) patient.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        if (dto.getBloodGroup() != null) patient.setBloodGroup(dto.getBloodGroup());
        if (dto.getAddress() != null) patient.setAddress(dto.getAddress());
        if (dto.getEmergencyContact() != null) patient.setEmergencyContact(dto.getEmergencyContact());
        if (dto.getMedicalHistory() != null) patient.setMedicalHistory(dto.getMedicalHistory());

        // Update user fields
        if (dto.getFullName() != null) patient.getUser().setFullName(dto.getFullName());
        if (dto.getPhone() != null) patient.getUser().setPhone(dto.getPhone());
        if (dto.getEmail() != null) patient.getUser().setEmail(dto.getEmail());

        patient = patientRepository.save(patient);
        logger.info("Patient updated successfully: {}", id);
        return toDTO(patient);
    }

    /** Delete patient (soft delete) */
    @Transactional
    public void deletePatient(Long id) {
        logger.info("Deleting patient ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        patient.getUser().setActive(false);
        patientRepository.save(patient);
        logger.info("Patient soft-deleted: {}", id);
    }

    /** Search patients by keyword */
    public List<PatientDTO> searchPatients(String keyword) {
        logger.info("Searching patients with keyword: {}", keyword);
        return patientRepository.searchPatients(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Convert entity to DTO */
    private PatientDTO toDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .userId(patient.getUser().getId())
                .fullName(patient.getUser().getFullName())
                .email(patient.getUser().getEmail())
                .phone(patient.getUser().getPhone())
                .dateOfBirth(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : null)
                .gender(patient.getGender() != null ? patient.getGender().name() : null)
                .bloodGroup(patient.getBloodGroup())
                .address(patient.getAddress())
                .emergencyContact(patient.getEmergencyContact())
                .medicalHistory(patient.getMedicalHistory())
                .active(patient.getUser().getActive())
                .build();
    }
}
