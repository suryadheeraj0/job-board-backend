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

import static org.assertj.core.api.Assertions.assertThat;
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

        List<JobResponse> response = jobService.getAllJobs();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getCompany()).isEqualTo("Acme");
    }

    @Test
    void searchJobsShouldDelegateToRepository() {
        Job job = Job.builder()
                .id(12L)
                .title("Frontend Engineer")
                .company("BetaCorp")
                .location("Kakinada")
                .jobType(JobType.FULL_TIME)
                .salary("60000")
                .description("Searchable job")
                .build();
        when(jobRepository.searchJobs("Engineer", "BetaCorp", "Kakinada", JobType.FULL_TIME))
                .thenReturn(List.of(job));

        List<JobResponse> result = jobService.searchJobs("Engineer", "BetaCorp", "Kakinada", JobType.FULL_TIME);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompany()).isEqualTo("BetaCorp");
    }
}
