package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.LoginRequest;
import com.JobBoard.example.demo.dto.RegisterRequest;
import com.JobBoard.example.demo.exception.InvalidEmailException;
import com.JobBoard.example.demo.exception.InvalidPasswordException;
import com.JobBoard.example.demo.exception.UserAlreadyExistsException;
import com.JobBoard.example.demo.model.Role;
import com.JobBoard.example.demo.model.User;
import com.JobBoard.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void registerShouldSaveUserWhenRequestIsValid() {
        RegisterRequest request = new RegisterRequest("User One", "user@example.com", "Password1!");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("user@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER.name());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void registerShouldThrowInvalidEmailExceptionForBadEmail() {
        RegisterRequest request = new RegisterRequest("User One", "bad-email", "Password1!");

        assertThrows(InvalidEmailException.class, () -> userService.register(request));
    }

    @Test
    void registerShouldThrowInvalidPasswordExceptionForWeakPassword() {
        RegisterRequest request = new RegisterRequest("User One", "user@example.com", "weak");

        assertThrows(InvalidPasswordException.class, () -> userService.register(request));
    }

    @Test
    void loginShouldReturnResponseWhenCredentialsMatch() {
        User user = User.builder()
                .id(10L)
                .name("User One")
                .email("login@example.com")
                .password("Password1!")
                .role(Role.USER)
                .build();
        when(userRepository.findByEmail("login@example.com")).thenReturn(Optional.of(user));

        var response = userService.login(new LoginRequest("login@example.com", "Password1!"));

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("login@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER.name());
    }
}
