package com.learnvault.learnvaultbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatResponse {

    private Long conversationId;
    private String answer;
    private List<SourceResponse> sources;
}
