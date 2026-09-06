package com.learnvault.learnvaultbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RetrievedChunkResponse {

    private Long chunkId;
    private String content;
    private Integer pageNumber;
    private Long documentId;
    private String documentName;
}
