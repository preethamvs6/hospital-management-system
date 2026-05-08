package com.hospital.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hospital Management System — Main Application Entry Point
 * 
 * This application provides a full-stack hospital management solution
 * with modules for Admin, Doctor, Patient, Appointment, and Billing management.
 */
@SpringBootApplication
public class HospitalManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalManagementApplication.class, args);
    }
}
