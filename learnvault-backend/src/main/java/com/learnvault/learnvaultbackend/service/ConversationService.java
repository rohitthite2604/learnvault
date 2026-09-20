package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.dto.ConversationResponse;
import com.learnvault.learnvaultbackend.dto.MessageResponse;
import com.learnvault.learnvaultbackend.exception.ResourceNotFoundException;
import com.learnvault.learnvaultbackend.model.Conversation;
import com.learnvault.learnvaultbackend.model.Message;
import com.learnvault.learnvaultbackend.repository.ConversationRepository;
import com.learnvault.learnvaultbackend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public List<ConversationResponse> getConversationsByKnowledgeSpace(Long knowledgeSpaceId) {
        List<Conversation> conversations = conversationRepository
                .findByKnowledgeSpaceIdOrderByUpdatedAtDesc(knowledgeSpaceId);

        return conversations.stream()
                .map(conversation -> new ConversationResponse(
                        conversation.getId(),
                        conversation.getTitle(),
                        conversation.getCreatedAt(),
                        conversation.getUpdatedAt()
                )).toList();
    }

    public List<MessageResponse> getMessagesByConversation(Long conversationId) {

        // Confirm that the conversation exists.
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Retrieve messages in chronological order.
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        // Convert entities into response DTOs.
        return messages.stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getRole(),
                        message.getContent(),
                        message.getCreatedAt()
                )).toList();
    }
}
