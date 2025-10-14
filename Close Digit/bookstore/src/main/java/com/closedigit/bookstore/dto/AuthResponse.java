package com.closedigit.bookstore.dto;

/**
 * Authentication response DTO
 */
public record AuthResponse(
        String token,
        String type,
        UserDto user
) {
    
    public static AuthResponse create(String token, UserDto user) {
        return new AuthResponse(token, "Bearer", user);
    }
}