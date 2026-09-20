package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.dto.ChatRequest;
import com.learnvault.learnvaultbackend.dto.ChatResponse;
import com.learnvault.learnvaultbackend.dto.RagResponse;
import com.learnvault.learnvaultbackend.exception.BadRequestException;
import com.learnvault.learnvaultbackend.exception.ResourceNotFoundException;
import com.learnvault.learnvaultbackend.model.Conversation;
import com.learnvault.learnvaultbackend.model.KnowledgeSpace;
import com.learnvault.learnvaultbackend.model.Message;
import com.learnvault.learnvaultbackend.repository.ConversationRepository;
import com.learnvault.learnvaultbackend.repository.KnowledgeSpaceRepository;
import com.learnvault.learnvaultbackend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final RagService ragService;
    private final KnowledgeSpaceRepository knowledgeSpaceRepository;


    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, RagService ragService, KnowledgeSpaceRepository knowledgeSpaceRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.ragService = ragService;
        this.knowledgeSpaceRepository = knowledgeSpaceRepository;
    }

    public ChatResponse chat(ChatRequest request) {

        Conversation conversation;

        if (request.getConversationId() == null) {

            KnowledgeSpace knowledgeSpace = knowledgeSpaceRepository
                    .findById(request.getKnowledgeSpaceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Knowledge Space not found"));

            conversation = new Conversation();
            conversation.setTitle(request.getMessage());
            conversation.setKnowledgeSpace(knowledgeSpace);

            conversation = conversationRepository.save(conversation);
        } else {
            conversation = conversationRepository
                    .findById(request.getConversationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

            if (!conversation.getKnowledgeSpace().getId().equals(request.getKnowledgeSpaceId())) {
                throw new BadRequestException("Conversation does not belong to the requested Knowledge Space");
            }
        }

        // 1. Get previous conversation history
        List<Message> previousMessages = messageRepository.
                findByConversationIdOrderByCreatedAtAsc(conversation.getId());

        String conversationHistory = previousMessages.stream()
                .map(message -> message.getRole() + ": " + message.getContent())
                .reduce("", (a, b) -> a + "\n" + b);

        // 2. Save current user message
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setRole(Message.Role.USER);
        userMessage.setContent(request.getMessage());

        messageRepository.save(userMessage);

        // 3. Generate RAG answer
        RagResponse ragResponse = ragService.answer(request.getMessage(), request.getKnowledgeSpaceId(), conversationHistory);


        // 4. Save assistant response
        Message assistantMessage = new Message();
        assistantMessage.setConversation(conversation);
        assistantMessage.setRole(Message.Role.ASSISTANT);
        assistantMessage.setContent(ragResponse.getAnswer());

        messageRepository.save(assistantMessage);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return new ChatResponse(
                conversation.getId(),
                ragResponse.getAnswer(),
                ragResponse.getSources()
        );
    }
}
