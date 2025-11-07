package com.example.webchecker;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {
    // Spring Data JPA will automatically create a method for us based on the name
    List<CheckResult> findTop20ByUrlOrderByTimestampDesc(String url); // automatically gives us a method to find the 20
                                                                      // most recent checks for a specific URL
}