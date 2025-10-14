package com.closedigit.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.closedigit.bookstore.dto.RegisterRequest;
import com.closedigit.bookstore.dto.UserDto;
import com.closedigit.bookstore.entity.Role;
import com.closedigit.bookstore.entity.User;
import com.closedigit.bookstore.exception.UserAlreadyExistsException;
import com.closedigit.bookstore.exception.UserNotFoundException;
import com.closedigit.bookstore.mapper.UserMapper;
import com.closedigit.bookstore.repository.UserRepository;

/**
 * Service class for User operations
 * Implements UserDetailsService for Spring Security integration
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserDto registerUser(RegisterRequest registerRequest) {
        logger.debug("Registering new user: {}", registerRequest.username());
        
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new UserAlreadyExistsException("Username already exists: " + registerRequest.username());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new UserAlreadyExistsException("Email already exists: " + registerRequest.email());
        }
        
        // Create new user with encoded password
        User user = new User(
                registerRequest.username(),
                registerRequest.email(),
                passwordEncoder.encode(registerRequest.password()),
                Role.USER // Default role for new users
        );
        
        User savedUser = userRepository.save(user);
        
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return userMapper.toDto(savedUser);
    }
    
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        logger.debug("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(Role role) {
        logger.debug("Fetching users with role: {}", role);
        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public UserDto updateUserRole(Long userId, Role newRole) {
        logger.debug("Updating role for user ID: {} to {}", userId, newRole);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        
        logger.info("User role updated successfully for user ID: {}", userId);
        return userMapper.toDto(updatedUser);
    }
    
    public void deleteUser(Long userId) {
        logger.debug("Deleting user with ID: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        
        userRepository.deleteById(userId);
        logger.info("User deleted successfully with ID: {}", userId);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long getUserCountByRole(Role role) {
        return userRepository.countByRole(role);
    }
}