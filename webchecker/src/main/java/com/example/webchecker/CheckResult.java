package com.example.webchecker;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // Tells Spring this class is a database table
public class CheckResult {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-increments the ID
    private Long id;

    private String url;
    private LocalDateTime timestamp;
    private boolean isUp;
    private String responseMessage;
    private long responseTimeMs;

    // Constructors, Getters, and Setters
    // Spring needs a no-arg constructor
    public CheckResult() {
    }

    // public CheckResult(String url, LocalDateTime timestamp, boolean isUp, String
    // responseMessage) {
    // this.url = url;
    // this.timestamp = timestamp;
    // this.isUp = isUp;
    // this.responseMessage = responseMessage;
    // }
    public CheckResult(String url, LocalDateTime timestamp, boolean isUp, String responseMessage, long responseTimeMs) {
        this.url = url;
        this.timestamp = timestamp;
        this.isUp = isUp;
        this.responseMessage = responseMessage;
        this.responseTimeMs = responseTimeMs; // NEW
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean isUp) {
        this.isUp = isUp;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }
}