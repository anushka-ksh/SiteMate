package com.example.webchecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebcheckerApplication {

    public static void main(String[] args) {
        // This line starts the entire web server
        SpringApplication.run(WebcheckerApplication.class, args);
    }

}