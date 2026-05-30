package com.JobBoard.example.demo.service;

import com.JobBoard.example.demo.dto.JobRequest;
import com.JobBoard.example.demo.dto.JobResponse;
import com.JobBoard.example.demo.model.Job;
import com.JobBoard.example.demo.model.JobType;
import com.JobBoard.example.demo.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    private JobServiceImpl jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobServiceImpl(jobRepository);
    }

    @Test
    void getAllJobsShouldReturnMappedResponses() {
        Job job = Job.builder()
                .id(11L)
                .title("Backend Engineer")
                .company("Acme")
                .location("Hyderabad")
                .jobType(JobType.FULL_TIME)
                .salary("50000")
                .description("Test job")
                .build();
        when(jobRepository.findAll()).thenReturn(List.of(job));

        List<JobResponse> responses = jobService.getAllJobs();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCompany()).isEqualTo("Acme");
        assertThat(responses.get(0).getTitle()).isEqualTo("Backend Engineer");
    }

    @Test
    void getJobByIdShouldReturnJobResponseWhenFound() {
        Job job = Job.builder()
                .id(12L)
                .title("Frontend Engineer")
                .company("BetaCorp")
                .location("Kakinada")
                .jobType(JobType.PART_TIME)
                .salary("60000")
                .description("Searchable job")
                .build();
        when(jobRepository.findById(12L)).thenReturn(Optional.of(job));

        JobResponse response = jobService.getJobById(12L);

        assertThat(response.getId()).isEqualTo(12L);
        assertThat(response.getCompany()).isEqualTo("BetaCorp");
    }

    @Test
    void createJobShouldSaveAndReturnResponse() {
        JobRequest request = new JobRequest("QA Engineer", "Gamma", "Visakhapatnam", JobType.CONTRACT, "50000", "Quality assurance");
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse response = jobService.createJob(request);

        assertThat(response).isNotNull();
        assertThat(response.getCompany()).isEqualTo("Gamma");
        assertThat(response.getTitle()).isEqualTo("QA Engineer");
    }

    @Test
    void searchJobsShouldDelegateToRepository() {
        Job job = Job.builder()
                .id(13L)
                .title("Full Stack")
                .company("Delta")
                .location("Vijayawada")
                .jobType(JobType.FULL_TIME)
                .salary("70000")
                .description("Full stack role")
                .build();
        when(jobRepository.searchJobs("Full", "Delta", "Vijayawada", JobType.FULL_TIME))
                .thenReturn(List.of(job));

        List<JobResponse> result = jobService.searchJobs("Full", "Delta", "Vijayawada", JobType.FULL_TIME);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompany()).isEqualTo("Delta");
        assertThat(result.get(0).getJobType()).isEqualTo(JobType.FULL_TIME);
    }
}
