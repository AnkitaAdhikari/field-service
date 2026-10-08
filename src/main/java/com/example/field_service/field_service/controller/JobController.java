package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.JobResponse;
import com.example.field_service.field_service.service.JobService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/me")
    public List<JobResponse> getMyJobs() {
        return jobService.getMyJobs();
    }

    @PatchMapping("/{id}/accept")
    public JobResponse accept(@PathVariable Long id) {
        return jobService.accept(id);
    }

    @PatchMapping("/{id}/start")
    public JobResponse start(@PathVariable Long id) {
        return jobService.start(id);
    }

    @PatchMapping("/{id}/complete")
    public JobResponse complete(@PathVariable Long id) {
        return jobService.complete(id);
    }

    @PatchMapping("/{id}/cancel")
    public JobResponse cancel(@PathVariable Long id) {
        return jobService.cancel(id);
    }
}