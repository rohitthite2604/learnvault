package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.model.Document;
import com.learnvault.learnvaultbackend.model.DocumentChunk;
import com.learnvault.learnvaultbackend.model.KnowledgeSpace;
import com.learnvault.learnvaultbackend.repository.DocumentChunkRepository;
import com.learnvault.learnvaultbackend.repository.DocumentRepository;
import com.learnvault.learnvaultbackend.repository.KnowledgeSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    private final PdfService pdfService;
    private final KnowledgeSpaceRepository knowledgeSpaceRepository;
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;

    public DocumentService(PdfService pdfService, KnowledgeSpaceRepository knowledgeSpaceRepository, DocumentRepository documentRepository, DocumentChunkRepository documentChunkRepository, EmbeddingService embeddingService) {
        this.pdfService = pdfService;
        this.knowledgeSpaceRepository = knowledgeSpaceRepository;
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
    }

    public Document processDocument(MultipartFile file, Long knowledgeSpaceId) throws IOException {

        KnowledgeSpace knowledgeSpace = knowledgeSpaceRepository.findById(knowledgeSpaceId)
                .orElseThrow(() -> new RuntimeException(
                        "Knowledge space not found"
                ));

        Document document = Document.builder()
                .knowledgeSpace(knowledgeSpace)
                .name(file.getOriginalFilename())
                .build();

        document = documentRepository.save(document);

        List<PageChunk> chunks = pdfService.extractAndChunkWithPages(file);

        for (PageChunk segment : chunks) {

            float[] embedding = embeddingService.generateEmbedding(segment.content());

            DocumentChunk chunk = DocumentChunk.builder()
                    .document(document)
                    .content(segment.content())
                    .pageNumber(segment.pageNumber())
                    .embedding(embedding)
                    .build();

            documentChunkRepository.save(chunk);
        }
        return document;
    }
}
