package com.incubyte.sweet.service;

import com.incubyte.sweet.dto.LoginRequest;
import com.incubyte.sweet.dto.LoginResponse;
import com.incubyte.sweet.dto.RegisterRequest;
import com.incubyte.sweet.entity.Role;
import com.incubyte.sweet.entity.User;
import com.incubyte.sweet.repository.UserRepository;
import com.incubyte.sweet.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest("test@example.com", "password123");
        validLoginRequest = new LoginRequest("test@example.com", "password123");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should register user with hashed password and USER role")
    void register_WithValidData_ShouldCreateUserWithHashedPasswordAndUserRole() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        authService.register(validRegisterRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("hashedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Should reject registration with duplicate email")
    void register_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_WithValidCredentials_ShouldReturnJwtToken() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "test@example.com", Role.USER))
                .thenReturn("jwt.token.here");

        // Act
        LoginResponse response = authService.login(validLoginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.token.here");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should reject login with incorrect password")
    void login_WithIncorrectPassword_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        LoginRequest wrongPasswordRequest = new LoginRequest("test@example.com", "wrongPassword");

        // Act & Assert
        assertThatThrownBy(() -> authService.login(wrongPasswordRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");

        verify(jwtTokenProvider, never()).generateToken(anyLong(), anyString(), any(Role.class));
    }

    @Test
    @DisplayName("Should reject login with non-existent email")
    void login_WithNonExistentEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        LoginRequest nonExistentRequest = new LoginRequest("nonexistent@example.com", "password123");

        // Act & Assert
        assertThatThrownBy(() -> authService.login(nonExistentRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should check email existence before registration")
    void register_ShouldCheckEmailExistence() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        // Act
        authService.register(validRegisterRequest);

        // Assert
        verify(userRepository).existsByEmail("test@example.com");
    }
}
