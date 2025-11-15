package com.example.webchecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {

            System.out.println("--- Creating default ADMIN user ---");

            // If not, create a new admin user
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpass")); // Set a default password
            adminUser.setRole("ADMIN"); // Set the role

            userRepository.save(adminUser);

            System.out.println("--- ADMIN user created (admin / adminpass) ---");
        }
    }
}