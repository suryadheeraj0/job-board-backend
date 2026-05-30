package com.JobBoard.example.demo.dto;

import com.JobBoard.example.demo.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private Long userId;
    private String resumePath;
    private String coverLetter;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private String jobTitle;
    private String company;
    private String candidateName;
    private String candidateEmail;
}
