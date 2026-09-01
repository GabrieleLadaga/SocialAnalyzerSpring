package com.unical.sdcc.service;

import com.unical.sdcc.utils.dto.AnalysisJobDTO;
import com.unical.sdcc.utils.dto.AnalysisRequestDTO;
import com.unical.sdcc.utils.enumeration.JobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AnalysisOrchestratorTest {

    @Mock
    private FirestoreService firestoreService;

    @Mock
    private PubSubService pubSubService;

    @InjectMocks
    private AnalysisOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartAnalysis() {
        AnalysisRequestDTO request = new AnalysisRequestDTO();
        request.setProfileURL("https://www.instagram.com/testuser/");

        doNothing().when(firestoreService).saveJob(any(AnalysisJobDTO.class));
        doNothing().when(pubSubService).publishMessage(anyString(), anyString());

        String jobId = orchestrator.startAnalysis(request);

        assertNotNull(jobId);
        assertFalse(jobId.isEmpty());
        verify(firestoreService, times(1)).saveJob(any(AnalysisJobDTO.class));
        verify(pubSubService, times(1)).publishMessage(eq(jobId), eq(request.getProfileURL()));
    }

    @Test
    void testGetJobStatus() {
        String jobId = "job-123";
        AnalysisJobDTO mockJob = new AnalysisJobDTO();
        mockJob.setJobID(jobId);
        mockJob.setStatus(JobStatus.PENDING);

        when(firestoreService.getJob(jobId)).thenReturn(mockJob);

        AnalysisJobDTO result = orchestrator.getJobStatus(jobId);

        assertNotNull(result);
        assertEquals(jobId, result.getJobID());
        assertEquals(JobStatus.PENDING, result.getStatus());
        verify(firestoreService, times(1)).getJob(jobId);
    }

}