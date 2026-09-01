package com.unical.sdcc.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.unical.sdcc.utils.dto.AnalysisJobDTO;
import com.unical.sdcc.utils.enumeration.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirestoreService {

    private final Firestore firestore;

    @Value("${FIRESTORE_COLLECTION_NAME:analysis_jobs}")
    private String collectionName;

    public void saveJob(AnalysisJobDTO job) {
        job.setCreatedAt(new Date());
        job.setUpdatedAt(new Date());
        DocumentReference docRef = firestore.collection(collectionName).document(job.getJobID());
        docRef.set(job);
    }

    public void updateJobStatus(String jobId, String status, String reportSummary, String riskLevel) {
        DocumentReference docRef = firestore.collection(collectionName).document(jobId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", new Date());
        if (reportSummary != null) {
            updates.put("reportSummary", reportSummary);
        }
        if (riskLevel != null) {
            updates.put("riskLevel", riskLevel);
        }
        docRef.update(updates);
    }

    public void updateJobError(String jobId, String errorMessage) {
        DocumentReference docRef = firestore.collection(collectionName).document(jobId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", JobStatus.FAILED.name());
        updates.put("errorMessage", errorMessage);
        updates.put("updatedAt", new Date());
        docRef.update(updates);
    }

    public AnalysisJobDTO getJob(String jobId) {
        try {
            DocumentSnapshot snapshot = firestore.collection(collectionName)
                    .document(jobId)
                    .get()
                    .get();
            if (snapshot.exists()) {
                return snapshot.toObject(AnalysisJobDTO.class);
            }
            return null;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public List<AnalysisJobDTO> getAllJobs() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
            QuerySnapshot snapshot = future.get();

            List<AnalysisJobDTO> jobs = new ArrayList<>();
            for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
                AnalysisJobDTO job = document.toObject(AnalysisJobDTO.class);
                jobs.add(job);
            }
            return jobs;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Errore nel recupero di tutti i job: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
