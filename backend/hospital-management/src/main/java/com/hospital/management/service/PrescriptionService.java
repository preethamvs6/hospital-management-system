package com.hospital.management.service;

import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.Prescription;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.PrescriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "id", id));
    }

    public Prescription getPrescriptionByAppointmentId(Long appointmentId) {
        return prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "appointmentId", appointmentId));
    }

    public List<Prescription> getPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    @Transactional
    public Prescription createOrUpdatePrescription(Long appointmentId, Prescription data) {
        logger.info("Creating/updating prescription for appointment ID: {}", appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElse(new Prescription());
        prescription.setAppointment(appointment);
        prescription.setMedications(data.getMedications());
        prescription.setDiagnosis(data.getDiagnosis());
        prescription.setNotes(data.getNotes());
        prescription.setPrescribedDate(LocalDate.now());
        prescription = prescriptionRepository.save(prescription);
        logger.info("Prescription saved with ID: {}", prescription.getId());
        return prescription;
    }

    public void deletePrescription(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prescription", "id", id);
        }
        prescriptionRepository.deleteById(id);
    }
}
