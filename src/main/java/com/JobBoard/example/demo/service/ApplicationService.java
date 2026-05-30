package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.ApplicationResponse;
import com.JobBoard.example.demo.dto.ApplicationUpdateRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
    List<ApplicationResponse> getAllApplications();
    ApplicationResponse getApplicationById(Long id);
    ApplicationResponse createApplication(Long jobId, Long userId, String coverLetter, MultipartFile resume);
    ApplicationResponse updateApplication(Long id, ApplicationUpdateRequest request);
    void deleteApplication(Long id);
    Resource loadResumeFile(Long id);
}
