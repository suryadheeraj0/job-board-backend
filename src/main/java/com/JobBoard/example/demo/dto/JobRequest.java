package com.JobBoard.example.demo.dto;

import com.JobBoard.example.demo.model.JobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private String title;
    private String company;
    private String location;
    private JobType jobType;
    private String salary;
    private String description;
}
