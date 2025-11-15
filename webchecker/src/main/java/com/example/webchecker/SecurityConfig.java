package com.example.webchecker;

import org.springframework.beans.factory.annotation.Autowired; // NEW
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- NEW: Inject the custom success handler ---
    @Autowired
    private CustomLoginSuccessHandler successHandler;

    // Bean 1: The Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean 2: The User Details Service
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }

    // Bean 3: The Security Filter Chain (The Main Rulebook)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow these pages to be visited by anyone
                        .requestMatchers("/", "/check", "/signup", "/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        // --- RULE FOR ADMINS ---
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // All other pages require the user to be logged in
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        // --- MODIFIED: Use the custom handler instead of defaultSuccessUrl ---
                        .successHandler(successHandler))
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll());

        // Required for H2 console
        http.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")));
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}