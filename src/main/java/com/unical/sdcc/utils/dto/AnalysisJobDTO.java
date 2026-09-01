package com.unical.sdcc.utils.dto;

import com.unical.sdcc.utils.enumeration.JobStatus;
import lombok.Data;

import java.util.Date;

@Data
public class AnalysisJobDTO {
    private String JobID;
    private String ProfileURL;
    private JobStatus status;
    private Date createdAt;
    private Date updatedAt;
    private String reportSummary;
    private String riskLevel;
    private String errorMessage;
}
