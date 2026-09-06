package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.dto.RetrievedChunkResponse;
import com.learnvault.learnvaultbackend.model.DocumentChunk;
import com.learnvault.learnvaultbackend.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RetrievalService {

    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository documentChunkRepository;

    public RetrievalService(EmbeddingService embeddingService, DocumentChunkRepository documentChunkRepository) {
        this.embeddingService = embeddingService;
        this.documentChunkRepository = documentChunkRepository;
    }

    public List<RetrievedChunkResponse> retrieve(String question, Long knowledgeSpaceId, int topK) {

        float[] queryEmbedding = embeddingService.generateEmbedding(question);

        String embeddingString = Arrays.toString(queryEmbedding);

        List<DocumentChunk> chunks = documentChunkRepository.findSimilarChunks(embeddingString, knowledgeSpaceId, topK);

        return chunks.stream()
                .map(chunk -> new RetrievedChunkResponse(
                        chunk.getId(),
                        chunk.getContent(),
                        chunk.getPageNumber(),
                        chunk.getDocument().getId(),
                        chunk.getDocument().getName()
                ))
                .toList();
    }
}
