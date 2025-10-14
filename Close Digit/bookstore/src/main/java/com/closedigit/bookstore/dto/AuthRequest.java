package com.closedigit.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Authentication request DTO
 */
public record AuthRequest(
        @NotBlank(message = "Username is required")
        String username,
        
        @NotBlank(message = "Password is required")
        String password
) {}