package com.hospital.management.repository;

import com.hospital.management.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Prescription entity.
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByAppointmentId(Long appointmentId);

    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.id = :patientId ORDER BY p.prescribedDate DESC")
    List<Prescription> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT p FROM Prescription p WHERE p.appointment.doctor.id = :doctorId ORDER BY p.prescribedDate DESC")
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);
}
