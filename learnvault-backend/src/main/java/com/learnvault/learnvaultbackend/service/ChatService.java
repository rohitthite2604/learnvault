package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.dto.ChatRequest;
import com.learnvault.learnvaultbackend.dto.ChatResponse;
import com.learnvault.learnvaultbackend.dto.RagResponse;
import com.learnvault.learnvaultbackend.model.Conversation;
import com.learnvault.learnvaultbackend.model.Message;
import com.learnvault.learnvaultbackend.repository.ConversationRepository;
import com.learnvault.learnvaultbackend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final RagService ragService;


    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, RagService ragService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.ragService = ragService;
    }

    public ChatResponse chat(ChatRequest request) {

        Conversation conversation;

        if (request.getConversationId() == null) {

            conversation = new Conversation();
            conversation.setTitle(request.getMessage());

            conversation = conversationRepository.save(conversation);
        } else {
            conversation = conversationRepository
                    .findById(request.getConversationId())
                    .orElseThrow(() ->
                            new RuntimeException("Conversation not found"));
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

        return new ChatResponse(
                conversation.getId(),
                ragResponse.getAnswer(),
                ragResponse.getSources()
        );
    }
}
