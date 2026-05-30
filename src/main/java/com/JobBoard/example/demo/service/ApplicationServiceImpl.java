package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.ApplicationResponse;
import com.JobBoard.example.demo.dto.ApplicationUpdateRequest;
import com.JobBoard.example.demo.exception.ApplicationAlreadyExistsException;
import com.JobBoard.example.demo.exception.ApplicationNotFoundException;
import com.JobBoard.example.demo.exception.InvalidFileException;
import com.JobBoard.example.demo.exception.JobNotFoundException;
import com.JobBoard.example.demo.exception.UserNotFoundException;
import com.JobBoard.example.demo.model.Application;
import com.JobBoard.example.demo.model.Job;
import com.JobBoard.example.demo.model.User;
import com.JobBoard.example.demo.repository.ApplicationRepository;
import com.JobBoard.example.demo.repository.JobRepository;
import com.JobBoard.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final Path uploadDirectory;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  JobRepository jobRepository,
                                  UserRepository userRepository,
                                  @Value("${file.upload-dir:uploads}") String uploadDir) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getApplicationsByUser(Long userId) {
        return applicationRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationResponse getApplicationById(Long id) {
        Application application = findApplicationById(id);
        return toResponse(application);
    }

    @Override
    public ApplicationResponse createApplication(Long jobId, Long userId, String coverLetter, MultipartFile resume) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (applicationRepository.findByJobIdAndUserId(jobId, userId).isPresent()) {
            throw new ApplicationAlreadyExistsException("You have already applied for this job");
        }

        String resumePath = storeResume(resume);

        Application application = Application.builder()
                .job(job)
                .user(user)
                .coverLetter(coverLetter)
                .resumePath(uploadDirectory.getFileName().toString() + "/" + resumePath)
                .status(com.JobBoard.example.demo.model.ApplicationStatus.APPLIED)
                .build();

        Application saved = applicationRepository.save(application);
        return toResponse(saved);
    }

    @Override
    public ApplicationResponse updateApplication(Long id, ApplicationUpdateRequest request) {
        Application application = findApplicationById(id);
        if (request.getCoverLetter() != null) {
            application.setCoverLetter(request.getCoverLetter());
        }
        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }
        return toResponse(applicationRepository.save(application));
    }

    @Override
    public void deleteApplication(Long id) {
        Application application = findApplicationById(id);
        applicationRepository.delete(application);
    }

    @Override
    public Resource loadResumeFile(Long id) {
        Application application = findApplicationById(id);
        Path filePath = uploadDirectory.resolve(Paths.get(application.getResumePath()).getFileName()).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new InvalidFileException("Resume file not found: " + application.getResumePath());
        } catch (MalformedURLException ex) {
            throw new InvalidFileException("Could not read resume file: " + ex.getMessage());
        }
    }

    private String storeResume(MultipartFile resume) {
        if (resume == null || resume.isEmpty()) {
            throw new InvalidFileException("Resume file is required");
        }

        String originalFileName = StringUtils.cleanPath(resume.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new InvalidFileException("Invalid resume file name");
        }

        String generatedFileName = System.currentTimeMillis() + "_" + originalFileName;
        Path targetLocation = uploadDirectory.resolve(generatedFileName);
        try {
            Files.copy(resume.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new InvalidFileException("Could not store resume file: " + ex.getMessage());
        }

        return generatedFileName;
    }

    private Application findApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));
    }

    private ApplicationResponse toResponse(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getUser().getId(),
                application.getResumePath(),
                application.getCoverLetter(),
                application.getStatus(),
                application.getAppliedAt(),
                application.getJob().getTitle(),
                application.getJob().getCompany(),
                application.getUser().getName(),
                application.getUser().getEmail()
        );
    }
}
