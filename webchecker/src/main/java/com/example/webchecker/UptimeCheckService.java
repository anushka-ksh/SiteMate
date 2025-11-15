package com.example.webchecker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime; // NEW import
import org.springframework.beans.factory.annotation.Autowired; // NEW import
import org.springframework.stereotype.Service;

@Service
public class UptimeCheckService {

    // --- NEW: Inject the repository ---
    private final CheckResultRepository repository;

    @Autowired
    public UptimeCheckService(CheckResultRepository repository) {
        this.repository = repository;
    }
    // --- End of new part ---

    // This method now saves the result before returning the message
    // Replace the entire checkUrlStatus method with this:
    public String checkUrlStatus(String urlString) {
        String responseMessage;
        boolean isUp = false;
        long responseTimeMs = -1; // Default to -1 for a failed check

        long startTime = System.currentTimeMillis(); // 1. Record start time

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();

            responseTimeMs = System.currentTimeMillis() - startTime; // 2. Calculate time
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                responseMessage = "[UP] " + urlString + " is online! (Response: 200 OK)";
                isUp = true;
            } else {
                responseMessage = "[WARN] " + urlString + " is online but... (Response: " + responseCode + ")";
                isUp = true;
            }

        } catch (IOException e) {
            responseTimeMs = System.currentTimeMillis() - startTime; // 2. Also calculate time on failure
            responseMessage = "[DOWN] " + urlString + " appears to be offline. (Error: " + e.getMessage() + ")";
            isUp = false;
        }

        // 3. Save the new data to the database
        CheckResult result = new CheckResult(urlString, LocalDateTime.now(), isUp, responseMessage, responseTimeMs);
        repository.save(result);

        return responseMessage;
    }
}