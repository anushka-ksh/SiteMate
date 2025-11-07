package com.example.webchecker;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonitoredSiteRepository extends JpaRepository<MonitoredSite, Long> {

    // Magic method: "Find all sites that belong to a specific User"
    List<MonitoredSite> findByUser(User user);
}