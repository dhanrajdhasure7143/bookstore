package com.closedigit.bookstore.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.closedigit.bookstore.dto.UserDto;
import com.closedigit.bookstore.entity.Role;
import com.closedigit.bookstore.service.UserService;

/**
 * REST Controller for User management operations
 * Admin-only endpoints for user management
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> getCurrentUserProfile(Authentication authentication) {
        logger.debug("Getting profile for user: {}", authentication.getName());

        UserDto user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users (Admin only)
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.debug("Getting all users");

        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID (Admin only)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        logger.debug("Getting user with ID: {}", id);

        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get users by role (Admin only)
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable Role role) {
        logger.debug("Getting users with role: {}", role);

        List<UserDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Update user role (Admin only)
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        logger.info("Updating role for user ID: {} to {}", id, role);

        UserDto updatedUser = userService.updateUserRole(id, role);

        logger.info("User role updated successfully for user ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);

        userService.deleteUser(id);

        logger.info("User deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get total users count (Admin only)
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalUsersCount() {
        logger.debug("Getting total users count");

        long count = userService.getTotalUsersCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get users count by role (Admin only)
     */
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> getUserCountByRole(@PathVariable Role role) {
        logger.debug("Getting user count for role: {}", role);

        long count = userService.getUserCountByRole(role);
        return ResponseEntity.ok(count);
    }
}