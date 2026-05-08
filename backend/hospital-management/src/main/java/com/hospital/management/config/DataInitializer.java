package com.hospital.management.config;

import com.hospital.management.entity.User;
import com.hospital.management.enums.Role;
import com.hospital.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@hospital.com")
                    .fullName("System Administrator")
                    .phone("1234567890")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            logger.info("Default admin user created: admin / admin123");
        }
    }
}
