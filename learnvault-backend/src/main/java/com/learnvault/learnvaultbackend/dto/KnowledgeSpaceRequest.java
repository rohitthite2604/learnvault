package com.learnvault.learnvaultbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnowledgeSpaceRequest {

    @NotBlank(message = "Knowledge Space name is required")
    @Size(max = 100, message = "Knowledge Space name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
