package com.example.webchecker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Correct import

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean 1: The Password Encoder
    // This tells Spring how to securely hash and check passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean 2: The User Details Service
    // This tells Spring Security how to find a user in our database.
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole()) // Spring will automatically add "ROLE_" prefix
                    .build();
        };
    }

    // Bean 3: The Security Filter Chain (The Main Rulebook)
    // This tells Spring Security which pages to protect and which to allow.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow these pages to be visited by anyone
                        .requestMatchers("/", "/check", "/signup", "/css/**", "/js/**").permitAll()
                        // H2 console is for development, allow it
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        // All other pages require the user to be logged in
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        // We will create a custom login page at "/login"
                        .loginPage("/login")
                        .permitAll()
                        // On successful login, redirect to a dashboard (we'll create this)
                        .defaultSuccessUrl("/dashboard", true))
                .logout(logout -> logout
                        // On logout, redirect to the homepage
                        .logoutSuccessUrl("/")
                        .permitAll());

        // Required for H2 console
        http.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")));
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}