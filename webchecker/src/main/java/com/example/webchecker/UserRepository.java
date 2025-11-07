package com.example.webchecker;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // This is a magic method Spring Data JPA will create for us.
    // It's how we'll find a user by their username during login.
    Optional<User> findByUsername(String username);
}