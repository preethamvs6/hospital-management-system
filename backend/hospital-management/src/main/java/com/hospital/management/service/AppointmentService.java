package com.hospital.management.service;

import com.hospital.management.dto.AppointmentDTO;
import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.Patient;
import com.hospital.management.enums.AppointmentStatus;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for appointment management — booking, cancellation, status updates.
 */
@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    /** Get all appointments */
    public List<AppointmentDTO> getAllAppointments() {
        logger.info("Fetching all appointments");
        return appointmentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Get appointment by ID */
    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        return toDTO(appointment);
    }

    /** Book a new appointment */
    @Transactional
    public AppointmentDTO bookAppointment(AppointmentDTO dto) {
        logger.info("Booking appointment: patient={}, doctor={}, date={}",
                dto.getPatientId(), dto.getDoctorId(), dto.getAppointmentDate());

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", dto.getPatientId()));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", dto.getDoctorId()));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.parse(dto.getAppointmentDate()))
                .appointmentTime(LocalTime.parse(dto.getAppointmentTime()))
                .status(AppointmentStatus.SCHEDULED)
                .reason(dto.getReason())
                .build();

        appointment = appointmentRepository.save(appointment);
        logger.info("Appointment booked with ID: {}", appointment.getId());
        return toDTO(appointment);
    }

    /** Cancel an appointment */
    @Transactional
    public AppointmentDTO cancelAppointment(Long id) {
        logger.info("Cancelling appointment ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);
        logger.info("Appointment cancelled: {}", id);
        return toDTO(appointment);
    }

    /** Complete an appointment */
    @Transactional
    public AppointmentDTO completeAppointment(Long id) {
        logger.info("Completing appointment ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);
        logger.info("Appointment completed: {}", id);
        return toDTO(appointment);
    }

    /** Get appointments by patient ID */
    public List<AppointmentDTO> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Get appointments by doctor ID */
    public List<AppointmentDTO> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Get appointments by status */
    public List<AppointmentDTO> getAppointmentsByStatus(String status) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRepository.findByStatus(appointmentStatus).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Get count by status */
    public long getCountByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }

    /** Convert entity to DTO */
    private AppointmentDTO toDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getUser().getFullName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getFullName())
                .doctorSpecialization(appointment.getDoctor().getSpecialization())
                .appointmentDate(appointment.getAppointmentDate().toString())
                .appointmentTime(appointment.getAppointmentTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm")))
                .status(appointment.getStatus().name())
                .reason(appointment.getReason())
                .createdAt(appointment.getCreatedAt() != null ? appointment.getCreatedAt().toString() : null)
                .build();
    }
}
