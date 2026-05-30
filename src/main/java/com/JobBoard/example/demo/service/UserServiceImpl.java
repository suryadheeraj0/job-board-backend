package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.AuthResponse;
import com.JobBoard.example.demo.dto.LoginRequest;
import com.JobBoard.example.demo.dto.RegisterRequest;
import com.JobBoard.example.demo.exception.InvalidCredentialsException;
import com.JobBoard.example.demo.exception.InvalidEmailException;
import com.JobBoard.example.demo.exception.InvalidPasswordException;
import com.JobBoard.example.demo.exception.UserAlreadyExistsException;
import com.JobBoard.example.demo.model.Role;
import com.JobBoard.example.demo.model.User;
import com.JobBoard.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PASSWORD_UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_LOWER = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String name = Optional.ofNullable(request.getName()).orElse("" ).trim();
        String email = Optional.ofNullable(request.getEmail()).orElse("" ).trim().toLowerCase();
        String password = Optional.ofNullable(request.getPassword()).orElse("" );

        if (name.isEmpty()) {
            throw new InvalidPasswordException("Name is required");
        }
        if (!isValidEmail(email)) {
            throw new InvalidEmailException("Invalid email address");
        }
        if (!isValidPassword(password)) {
            throw new InvalidPasswordException("Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("A user with that email already exists");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return new AuthResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole().name(), "Registration successful");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = Optional.ofNullable(request.getEmail()).orElse("" ).trim().toLowerCase();
        String password = Optional.ofNullable(request.getPassword()).orElse("" );

        if (email.isEmpty() || password.isEmpty()) {
            throw new InvalidCredentialsException("Email and password are required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), "Login successful");
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password != null
                && password.length() >= 8
                && PASSWORD_DIGIT.matcher(password).matches()
                && PASSWORD_UPPER.matcher(password).matches()
                && PASSWORD_LOWER.matcher(password).matches()
                && PASSWORD_SPECIAL.matcher(password).matches();
    }
}
