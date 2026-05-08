package com.hospital.management.service;

import com.hospital.management.dto.DoctorDTO;
import com.hospital.management.entity.Doctor;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for doctor management — CRUD operations, search, and DTO conversion.
 */
@Service
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    @Autowired
    private DoctorRepository doctorRepository;

    /** Get all doctors */
    public List<DoctorDTO> getAllDoctors() {
        logger.info("Fetching all doctors");
        return doctorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Get doctor by ID */
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        return toDTO(doctor);
    }

    /** Get doctor by user ID */
    public DoctorDTO getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
        return toDTO(doctor);
    }

    /** Update doctor profile */
    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {
        logger.info("Updating doctor ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));

        if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
        if (dto.getQualification() != null) doctor.setQualification(dto.getQualification());
        if (dto.getLicenseNumber() != null) doctor.setLicenseNumber(dto.getLicenseNumber());
        if (dto.getConsultationFee() != null) doctor.setConsultationFee(dto.getConsultationFee());
        if (dto.getAvailableDays() != null) doctor.setAvailableDays(dto.getAvailableDays());
        if (dto.getAvailableTime() != null) doctor.setAvailableTime(dto.getAvailableTime());
        if (dto.getDepartment() != null) doctor.setDepartment(dto.getDepartment());

        // Update user fields
        if (dto.getFullName() != null) doctor.getUser().setFullName(dto.getFullName());
        if (dto.getPhone() != null) doctor.getUser().setPhone(dto.getPhone());
        if (dto.getEmail() != null) doctor.getUser().setEmail(dto.getEmail());

        doctor = doctorRepository.save(doctor);
        logger.info("Doctor updated successfully: {}", id);
        return toDTO(doctor);
    }

    /** Delete doctor */
    @Transactional
    public void deleteDoctor(Long id) {
        logger.info("Deleting doctor ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        doctor.getUser().setActive(false); // Soft delete
        doctorRepository.save(doctor);
        logger.info("Doctor soft-deleted: {}", id);
    }

    /** Search doctors by keyword */
    public List<DoctorDTO> searchDoctors(String keyword) {
        logger.info("Searching doctors with keyword: {}", keyword);
        return doctorRepository.searchDoctors(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Convert entity to DTO */
    private DoctorDTO toDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .fullName(doctor.getUser().getFullName())
                .email(doctor.getUser().getEmail())
                .phone(doctor.getUser().getPhone())
                .specialization(doctor.getSpecialization())
                .qualification(doctor.getQualification())
                .licenseNumber(doctor.getLicenseNumber())
                .consultationFee(doctor.getConsultationFee())
                .availableDays(doctor.getAvailableDays())
                .availableTime(doctor.getAvailableTime())
                .department(doctor.getDepartment())
                .active(doctor.getUser().getActive())
                .build();
    }
}
