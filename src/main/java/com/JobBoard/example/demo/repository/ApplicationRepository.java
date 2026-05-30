package com.JobBoard.example.demo.repository;

import com.JobBoard.example.demo.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByJobIdAndUserId(Long jobId, Long userId);
    List<Application> findByJobId(Long jobId);
    List<Application> findByUserId(Long userId);
}
