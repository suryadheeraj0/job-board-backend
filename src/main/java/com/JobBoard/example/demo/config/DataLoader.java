package com.JobBoard.example.demo.config;

import com.JobBoard.example.demo.model.Role;
import com.JobBoard.example.demo.model.User;
import com.JobBoard.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner init() {

        return args -> {

            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {

                User admin = User.builder()
                        .name("Admin")
                        .email("admin@gmail.com")
                        .password("Admin@123")
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(admin);

                System.out.println("Admin user created");
            }
        };
    }
}