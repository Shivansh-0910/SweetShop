package com.incubyte.sweet.service;

import com.incubyte.sweet.dto.LoginRequest;
import com.incubyte.sweet.dto.LoginResponse;
import com.incubyte.sweet.dto.RegisterRequest;

public interface AuthService {
    
    void register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
}
