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
    public String checkUrlStatus(String urlString) {
        String responseMessage;
        boolean isUp = false; // Variable to track the status

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                responseMessage = "[UP] " + urlString + " is online! (Response: 200 OK)";
                isUp = true; // Set status
            } else {
                responseMessage = "[WARN] " + urlString + " is online but returned a non-OK status. (Response: "
                        + responseCode + ")";
                isUp = true; // Still "up" because it responded
            }

        } catch (IOException e) {
            responseMessage = "[DOWN] " + urlString + " appears to be offline. (Error: " + e.getMessage() + ")";
            // isUp remains false
        }

        // --- NEW: Save the result to the database ---
        CheckResult result = new CheckResult(urlString, LocalDateTime.now(), isUp, responseMessage);
        repository.save(result);
        // --- End of new part ---

        return responseMessage;
    }
}