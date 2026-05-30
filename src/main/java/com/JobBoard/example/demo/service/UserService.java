package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.AuthResponse;
import com.JobBoard.example.demo.dto.LoginRequest;
import com.JobBoard.example.demo.dto.RegisterRequest;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
