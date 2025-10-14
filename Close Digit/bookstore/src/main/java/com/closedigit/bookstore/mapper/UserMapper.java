package com.closedigit.bookstore.mapper;

import com.closedigit.bookstore.dto.UserDto;
import com.closedigit.bookstore.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entity and UserDto
 * Excludes sensitive information like password from DTOs
 */
@Component
public class UserMapper {
    
    /**
     * Convert User entity to UserDto (excluding password)
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    
    /**
     * Convert UserDto to User entity (password needs to be set separately)
     */
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDto.id());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setRole(userDto.role());
        return user;
    }
}