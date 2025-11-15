package com.example.webchecker;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// This is a special controller that returns data (like JSON) instead of HTML.
@RestController
public class ApiHistoryController {

    @Autowired
    private MonitoredSiteRepository siteRepository;

    @Autowired
    private CheckResultRepository resultRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/history/{siteId}")
    public ResponseEntity<List<CheckResult>> getSiteHistory(@PathVariable("siteId") Long siteId, Principal principal) {

        // 1. Get the logged-in user
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "User not found"));

        // 2. Find the site
        MonitoredSite site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid site Id:" + siteId));

        // 3. SECURITY CHECK: Make sure the site belongs to the logged-in user
        if (!site.getUser().getId().equals(user.getId())) {
            // If not, return "Forbidden"
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4. Fetch the history for that site's URL
        List<CheckResult> history = resultRepository.findTop20ByUrlOrderByTimestampDesc(site.getUrl());

        // 5. Return the history as JSON data
        return ResponseEntity.ok(history);
    }
}