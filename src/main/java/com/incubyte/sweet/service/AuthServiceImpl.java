package com.incubyte.sweet.service;

import com.incubyte.sweet.dto.LoginRequest;
import com.incubyte.sweet.dto.LoginResponse;
import com.incubyte.sweet.dto.RegisterRequest;
import com.incubyte.sweet.entity.Role;
import com.incubyte.sweet.entity.User;
import com.incubyte.sweet.repository.UserRepository;
import com.incubyte.sweet.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        log.debug("Registering user with email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create new user with hashed password
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);  // Default role
        
        userRepository.save(user);
        log.info("User registered successfully: {}", request.getEmail());
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        log.info("User logged in successfully: {}", request.getEmail());
        
        return new LoginResponse(token, user.getEmail(), user.getRole().name());
    }
}
