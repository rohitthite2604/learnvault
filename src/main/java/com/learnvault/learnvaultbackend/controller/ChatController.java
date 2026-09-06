package com.learnvault.learnvaultbackend.controller;

import com.learnvault.learnvaultbackend.dto.ChatRequest;
import com.learnvault.learnvaultbackend.dto.ChatResponse;
import com.learnvault.learnvaultbackend.dto.RagResponse;
import com.learnvault.learnvaultbackend.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}
