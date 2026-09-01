package com.unical.sdcc.controller;

import com.unical.sdcc.service.AnalysisOrchestrator;
import com.unical.sdcc.utils.dto.AnalysisJobDTO;
import com.unical.sdcc.utils.dto.AnalysisRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisOrchestrator orchestrator;

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startAnalysis(@Valid @RequestBody AnalysisRequestDTO request) {
        String jobId = orchestrator.startAnalysis(request);
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<AnalysisJobDTO> getStatus(@PathVariable String jobId) {
        AnalysisJobDTO job = orchestrator.getJobStatus(jobId);
        if(job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(job);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<AnalysisJobDTO>> getAllJobs() {
        List<AnalysisJobDTO> jobs = orchestrator.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

}
