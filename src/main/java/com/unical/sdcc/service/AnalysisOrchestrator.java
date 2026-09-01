package com.unical.sdcc.service;

import com.unical.sdcc.utils.dto.AnalysisJobDTO;
import com.unical.sdcc.utils.dto.AnalysisRequestDTO;
import com.unical.sdcc.utils.enumeration.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisOrchestrator {

    private final FirestoreService firestoreService;

    private final PubSubService pubSubService;

    public String startAnalysis(AnalysisRequestDTO request) {
        String jobId = UUID.randomUUID().toString();

        AnalysisJobDTO job = new AnalysisJobDTO();
        job.setJobID(jobId);
        job.setProfileURL(request.getProfileURL());
        job.setStatus(JobStatus.PENDING);

        firestoreService.saveJob(job);

        pubSubService.publishMessage(jobId, request.getProfileURL());

        log.info("Job started with ID: {}", jobId);
        return jobId;
    }

    public AnalysisJobDTO getJobStatus(String jobId) {
        return firestoreService.getJob(jobId);
    }

    public List<AnalysisJobDTO> getAllJobs() {
        return firestoreService.getAllJobs();
    }

}
