package com.JobBoard.example.demo.dto;

import com.JobBoard.example.demo.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUpdateRequest {
    private String coverLetter;
    private ApplicationStatus status;
}
