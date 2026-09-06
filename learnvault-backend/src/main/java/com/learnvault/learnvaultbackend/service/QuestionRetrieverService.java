package com.learnvault.learnvaultbackend.service;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class QuestionRetrieverService {

    private final ChatModel chatModel;

    public QuestionRetrieverService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String rewrite(String question, String conversationHistory) {
        if (conversationHistory == null || conversationHistory.isBlank()) {
            return question;
        }

        String prompt = """
                Rewrite the user's current question into a standalone question
                that can be understood without the conversation history.
                
                Preserve the user's original intent.
                Resolve references such as "it", "its", "they", "this", etc.
                using the conversation history.
                
                Do NOT answer the question.
                Return only the rewritten question.
                
                Conversation history:
                %s
                
                Current question:
                %s
                
                Standalone question:
                """.formatted(conversationHistory, question);

        return chatModel.chat(prompt).trim();
    }
}
