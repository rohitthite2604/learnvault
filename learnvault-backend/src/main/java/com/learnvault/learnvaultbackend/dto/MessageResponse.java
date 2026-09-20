package com.learnvault.learnvaultbackend.dto;

import com.learnvault.learnvaultbackend.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private Message.Role role;
    private String content;
    private LocalDateTime createdAt;
}
