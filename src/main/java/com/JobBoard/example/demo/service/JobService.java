package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.JobRequest;
import com.JobBoard.example.demo.dto.JobResponse;
import com.JobBoard.example.demo.model.JobType;

import java.util.List;

public interface JobService {
    List<JobResponse> getAllJobs();
    List<JobResponse> searchJobs(String title, String company, String location, JobType jobType);
    JobResponse getJobById(Long id);
    JobResponse createJob(JobRequest request);
    JobResponse updateJob(Long id, JobRequest request);
    void deleteJob(Long id);
}
