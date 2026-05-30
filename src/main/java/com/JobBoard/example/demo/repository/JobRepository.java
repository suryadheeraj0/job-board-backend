package com.JobBoard.example.demo.repository;

import com.JobBoard.example.demo.model.Job;
import com.JobBoard.example.demo.model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE " +
            "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:company IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType)")
    List<Job> searchJobs(
            @Param("title") String title,
            @Param("company") String company,
            @Param("location") String location,
            @Param("jobType") JobType jobType
    );
}
