package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.dto.RagResponse;
import com.learnvault.learnvaultbackend.dto.RetrievedChunkResponse;
import com.learnvault.learnvaultbackend.dto.SourceResponse;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final RetrievalService retrievalService;
    private final ChatModel chatModel;
    private final QuestionRetrieverService questionRetrieverService;

    public RagService(RetrievalService retrievalService, ChatModel chatModel, QuestionRetrieverService questionRetrieverService) {
        this.retrievalService = retrievalService;
        this.chatModel = chatModel;
        this.questionRetrieverService = questionRetrieverService;
    }

    public RagResponse answer(String question, Long knowledgeSpaceId,
                              String conversationHistory) {

        String retrievalQuestion = questionRetrieverService.rewrite(question, conversationHistory);

        List<RetrievedChunkResponse> chunks = retrievalService.retrieve(retrievalQuestion, knowledgeSpaceId, 5);

//        System.out.println("========== RAG DEBUG ==========");
//        System.out.println("Original question: " + question);
//        System.out.println("Retrieval question: " + retrievalQuestion);
//
//        System.out.println("Conversation History:");
//        System.out.println(conversationHistory);
//
//        System.out.println("Retrieved Chunks:");
//
//        for (RetrievedChunkResponse chunk : chunks) {
//            System.out.println(
//                    "Document: " + chunk.getDocumentName()
//                            + " | Page: " + chunk.getPageNumber()
//            );
//
//            System.out.println(chunk.getContent());
//            System.out.println("--------------------------------");
//        }
//
//        System.out.println("========== END DEBUG ==========");

        String context = chunks.stream()
                .map(RetrievedChunkResponse::getContent)
                .reduce("", (a,b) -> a + "\n\n" + b);

        String prompt = """
                You are a helpful AI learning assistant.
                
                Answer the user's question using ONLY the provided document context.
                
                If the answer cannot be found in the context, say:
                "I don't have enough information in the provided documents."
                
                Do not make up information.
                
                Conversation history:
                %s
                
                Document context:
                %s
                
                Current question:
                %s
                
                Answer:
                """.formatted(conversationHistory, context, question);

//        System.out.println("========== FINAL PROMPT ==========");
//        System.out.println(prompt);
//        System.out.println("========== END FINAL PROMPT ==========");

        String answer = chatModel.chat(prompt);

        List<SourceResponse> sources = chunks.stream()
                .map(chunk -> new SourceResponse(
                        chunk.getDocumentId(),
                        chunk.getDocumentName(),
                        chunk.getPageNumber()
                ))
                .toList();

        return new RagResponse(answer, sources);
    }
}
