package com.learnvault.learnvaultbackend.dto;

import lombok.Data;

@Data
public class ChatRequest {

    private Long conversationId;
    private Long knowledgeSpaceId;
    private String message;

}
