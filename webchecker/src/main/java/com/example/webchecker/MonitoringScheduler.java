package com.example.webchecker;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component // A Spring bean, similar to @Service
@EnableScheduling // This annotation is crucial, it turns on the scheduler
public class MonitoringScheduler {

    private final MonitoredSiteRepository siteRepository;
    private final UptimeCheckService uptimeCheckService;

    @Autowired
    public MonitoringScheduler(MonitoredSiteRepository siteRepository, UptimeCheckService uptimeCheckService) {
        this.siteRepository = siteRepository;
        this.uptimeCheckService = uptimeCheckService;
    }

    // This method will run automatically
    // fixedRate = 600000 milliseconds = 10 minutes
    @Scheduled(fixedRate = 10000) // 10,000 milliseconds = 10 seconds
    public void checkAllSites() {
        System.out.println("--- SCHEDULER: Starting check for all sites... ---");

        // 1. Get ALL sites from the database
        List<MonitoredSite> allSites = siteRepository.findAll();

        // 2. Loop through each one and check it
        for (MonitoredSite site : allSites) {
            System.out.println("Checking: " + site.getUrl());

            // 3. We call our existing service. It automatically saves the result.
            uptimeCheckService.checkUrlStatus(site.getUrl());
        }

        System.out.println("--- SCHEDULER: All sites checked. ---");
    }
}