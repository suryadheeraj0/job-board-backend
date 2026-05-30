package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.JobRequest;
import com.JobBoard.example.demo.dto.JobResponse;
import com.JobBoard.example.demo.exception.JobNotFoundException;
import com.JobBoard.example.demo.model.Job;
import com.JobBoard.example.demo.model.JobType;
import com.JobBoard.example.demo.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> searchJobs(String title, String company, String location, JobType jobType) {
        return jobRepository.searchJobs(title, company, location, jobType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJobById(Long id) {
        Job job = findJobById(id);
        return toResponse(job);
    }

    @Override
    public JobResponse createJob(JobRequest request) {
        Job job = Job.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .salary(request.getSalary())
                .description(request.getDescription())
                .build();

        Job savedJob = jobRepository.save(job);
        return toResponse(savedJob);
    }

    @Override
    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = findJobById(id);
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSalary(request.getSalary());
        job.setDescription(request.getDescription());

        Job updatedJob = jobRepository.save(job);
        return toResponse(updatedJob);
    }

    @Override
    public void deleteJob(Long id) {
        Job job = findJobById(id);
        jobRepository.delete(job);
    }

    private Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getJobType(),
                job.getSalary(),
                job.getDescription(),
                job.getCreatedAt()
        );
    }
}
