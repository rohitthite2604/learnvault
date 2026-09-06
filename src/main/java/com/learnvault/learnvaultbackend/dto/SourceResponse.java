package com.learnvault.learnvaultbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SourceResponse {

    private Long documentId;
    private String documentName;
    private Integer pageNumber;
}
