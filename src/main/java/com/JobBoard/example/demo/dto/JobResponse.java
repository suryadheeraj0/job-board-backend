package com.JobBoard.example.demo.dto;

import com.JobBoard.example.demo.model.JobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String company;
    private String location;
    private JobType jobType;
    private String salary;
    private String description;
    private LocalDateTime createdAt;
}
