package com.unical.sdcc.controller;

import com.unical.sdcc.service.AnalysisOrchestrator;
import com.unical.sdcc.utils.dto.AnalysisJobDTO;
import com.unical.sdcc.utils.dto.AnalysisRequestDTO;
import com.unical.sdcc.utils.enumeration.JobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnalysisControllerTest {

    @Mock
    private AnalysisOrchestrator orchestrator;

    @InjectMocks
    private AnalysisController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartAnalysis() {
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setProfileURL("https://www.instagram.com/testuser/");
        String expectedJobId = "job-123";

        when(orchestrator.startAnalysis(any(AnalysisRequestDTO.class))).thenReturn(expectedJobId);

        ResponseEntity<Map<String, String>> response = controller.startAnalysis(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedJobId, response.getBody().get("jobId"));
        verify(orchestrator, times(1)).startAnalysis(any(AnalysisRequestDTO.class));
    }

    @Test
    void testGetStatus() {
        String jobId = "job-123";
        AnalysisJobDTO mockJob = new AnalysisJobDTO();
        mockJob.setJobID(jobId);
        mockJob.setStatus(JobStatus.COMPLETED);
        mockJob.setProfileURL("https://www.instagram.com/testuser/");
        mockJob.setCreatedAt(new Date());

        when(orchestrator.getJobStatus(jobId)).thenReturn(mockJob);

        ResponseEntity<AnalysisJobDTO> response = controller.getStatus(jobId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jobId, response.getBody().getJobID());
        assertEquals(JobStatus.COMPLETED, response.getBody().getStatus());
        verify(orchestrator, times(1)).getJobStatus(jobId);
    }

    @Test
    void testGetStatus_NotFound() {
        String jobId = "non-existent";
        when(orchestrator.getJobStatus(jobId)).thenReturn(null);

        ResponseEntity<AnalysisJobDTO> response = controller.getStatus(jobId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orchestrator, times(1)).getJobStatus(jobId);
    }

}