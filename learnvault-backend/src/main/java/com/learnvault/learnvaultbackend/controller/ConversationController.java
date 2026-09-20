package com.learnvault.learnvaultbackend.controller;

import com.learnvault.learnvaultbackend.dto.ConversationResponse;
import com.learnvault.learnvaultbackend.dto.MessageResponse;
import com.learnvault.learnvaultbackend.service.ConversationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public List<ConversationResponse> getConversations(@RequestParam Long knowledgeSpaceId) {
        return conversationService.getConversationsByKnowledgeSpace(knowledgeSpaceId);
    }

    @GetMapping("/{conversationId}/messages")
    public List<MessageResponse> getMessages(@PathVariable Long conversationId) {
        return conversationService.getMessagesByConversation(conversationId);
    }
}
