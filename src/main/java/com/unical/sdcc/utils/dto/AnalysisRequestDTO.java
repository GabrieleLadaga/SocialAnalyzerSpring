package com.unical.sdcc.utils.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnalysisRequestDTO {
    @NotBlank(message = "Profile URL cannot be empty")
    private String profileURL;
}
