package com.closedigit.bookstore.dto;

import java.time.LocalDateTime;

import com.closedigit.bookstore.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User DTO
 */
public record UserDto(
        Long id,
        
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    
    public static UserDto fromUser(Long id, String username, String email, Role role, 
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new UserDto(id, username, email, role, createdAt, updatedAt);
    }
}