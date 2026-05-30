package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.ApplicationResponse;
import com.JobBoard.example.demo.dto.ApplicationUpdateRequest;
import com.JobBoard.example.demo.exception.ApplicationAlreadyExistsException;
import com.JobBoard.example.demo.exception.ApplicationNotFoundException;
import com.JobBoard.example.demo.exception.InvalidFileException;
import com.JobBoard.example.demo.model.Application;
import com.JobBoard.example.demo.model.ApplicationStatus;
import com.JobBoard.example.demo.model.Job;
import com.JobBoard.example.demo.model.JobType;
import com.JobBoard.example.demo.model.User;
import com.JobBoard.example.demo.repository.ApplicationRepository;
import com.JobBoard.example.demo.repository.JobRepository;
import com.JobBoard.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @TempDir
    Path tempDir;

    private ApplicationServiceImpl applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ApplicationServiceImpl(applicationRepository, jobRepository, userRepository, tempDir.toString());
    }

    @Test
    void createApplicationShouldSaveAndReturnResponse() {
        Job job = Job.builder().id(3L).title("QA Engineer").company("Gamma").location("Visakhapatnam").jobType(JobType.FULL_TIME).build();
        User user = User.builder().id(7L).name("Applicant").email("applicant@example.com").password("Password1!").role(null).build();

        when(jobRepository.findById(3L)).thenReturn(Optional.of(job));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(applicationRepository.findByJobIdAndUserId(3L, 7L)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
            Application saved = invocation.getArgument(0);
            saved.setId(5L);
            saved.setAppliedAt(LocalDateTime.now());
            return saved;
        });

        MockMultipartFile resume = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "file-content".getBytes());
        ApplicationResponse response = applicationService.createApplication(3L, 7L, "I am interested", resume);

        assertThat(response).isNotNull();
        assertThat(response.getJobId()).isEqualTo(3L);
        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getResumePath()).contains(tempDir.getFileName().toString());
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
    }

    @Test
    void createApplicationShouldThrowWhenDuplicateExists() {
        Job job = Job.builder().id(3L).title("QA Engineer").company("Gamma").location("Visakhapatnam").jobType(JobType.FULL_TIME).build();
        User user = User.builder().id(7L).name("Applicant").email("applicant@example.com").password("Password1!").role(null).build();

        when(jobRepository.findById(3L)).thenReturn(Optional.of(job));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(applicationRepository.findByJobIdAndUserId(3L, 7L)).thenReturn(Optional.of(new Application()));

        MockMultipartFile resume = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "file-content".getBytes());

        assertThrows(ApplicationAlreadyExistsException.class,
                () -> applicationService.createApplication(3L, 7L, "I am interested", resume));
    }

    @Test
    void updateApplicationShouldModifyCoverLetterAndStatus() {
        Job job = Job.builder().id(3L).title("QA Engineer").company("Gamma").location("Visakhapatnam").jobType(JobType.FULL_TIME).build();
        User user = User.builder().id(7L).name("Applicant").email("applicant@example.com").password("Password1!").role(null).build();
        Application application = new Application();
        application.setId(10L);
        application.setJob(job);
        application.setUser(user);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCoverLetter("Old letter");

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponse response = applicationService.updateApplication(10L, new ApplicationUpdateRequest("New letter", ApplicationStatus.SHORTLISTED));

        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.SHORTLISTED);
        assertThat(response.getResumePath()).isNull();
        assertThat(response.getCoverLetter()).isEqualTo("New letter");
    }

    @Test
    void loadResumeFileShouldReturnFileResource() throws Exception {
        Path resumeFile = tempDir.resolve("resume.pdf");
        Files.writeString(resumeFile, "dummy");

        Application application = new Application();
        application.setId(22L);
        application.setResumePath(tempDir.getFileName().toString() + "/resume.pdf");

        when(applicationRepository.findById(22L)).thenReturn(Optional.of(application));

        var resource = applicationService.loadResumeFile(22L);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.getFilename()).isEqualTo("resume.pdf");
    }
}
