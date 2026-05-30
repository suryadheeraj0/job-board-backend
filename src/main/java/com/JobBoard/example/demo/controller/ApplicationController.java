package com.JobBoard.example.demo.controller;

import com.JobBoard.example.demo.dto.ApplicationResponse;
import com.JobBoard.example.demo.dto.ApplicationUpdateRequest;
import com.JobBoard.example.demo.service.ApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @PostMapping(path = "/postApplication",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationResponse> createApplication(
            @RequestParam Long jobId,
            @RequestParam Long userId,
            @RequestParam(required = false) String coverLetter,
            @RequestPart("resume") MultipartFile resume) {
        ApplicationResponse response = applicationService.createApplication(jobId, userId, coverLetter, resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/updateApplication/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @PathVariable Long id,
            @RequestBody ApplicationUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateApplication(id, request));
    }

    @DeleteMapping("/deleteApplication/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) throws IOException {
        Resource resource = applicationService.loadResumeFile(id);
        String filename = resource.getFilename();

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
