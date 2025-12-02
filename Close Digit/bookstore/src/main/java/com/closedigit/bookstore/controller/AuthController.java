package com.closedigit.bookstore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.closedigit.bookstore.dto.AuthRequest;
import com.closedigit.bookstore.dto.AuthResponse;
import com.closedigit.bookstore.dto.RegisterRequest;
import com.closedigit.bookstore.service.AuthService;


import jakarta.validation.Valid;

/**
 * Authentication controller for login and signup 
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)

public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("Login attempt for user: {}", authRequest.username());

        AuthResponse authResponse = authService.login(authRequest);

        logger.info("Login successful for user: {}", authRequest.username());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registration attempt for user: {}", registerRequest.username());

        AuthResponse authResponse = authService.register(registerRequest);

        logger.info("Registration successful for user: {}", registerRequest.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }
}