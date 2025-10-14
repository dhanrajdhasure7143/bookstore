package com.closedigit.bookstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.closedigit.bookstore.dto.AuthRequest;
import com.closedigit.bookstore.dto.AuthResponse;
import com.closedigit.bookstore.dto.RegisterRequest;
import com.closedigit.bookstore.dto.UserDto;
import com.closedigit.bookstore.security.JwtUtil;

/**
 * Authentication service for handling login and registration
 */
@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest authRequest) {
        logger.debug("Attempting to authenticate user: {}", authRequest.username());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.username(),
                            authRequest.password()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            String token = jwtUtil.generateToken(userDetails);
            
            UserDto userDto = userService.getUserByUsername(authRequest.username());
            
            logger.info("User authenticated successfully: {}", authRequest.username());
            return AuthResponse.create(token, userDto);
            
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}", authRequest.username());
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    
    public AuthResponse register(RegisterRequest registerRequest) {
        logger.debug("Attempting to register user: {}", registerRequest.username());
        
        UserDto userDto = userService.registerUser(registerRequest);
        
        AuthRequest authRequest = new AuthRequest(registerRequest.username(), registerRequest.password());
        
        logger.info("User registered successfully: {}", registerRequest.username());
        return login(authRequest);
    }
}