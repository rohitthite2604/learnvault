package com.learnvault.learnvaultbackend.controller;

import com.learnvault.learnvaultbackend.dto.RagResponse;
import com.learnvault.learnvaultbackend.dto.RetrievedChunkResponse;
import com.learnvault.learnvaultbackend.model.Document;
import com.learnvault.learnvaultbackend.model.DocumentChunk;
import com.learnvault.learnvaultbackend.service.*;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final PdfService pdfService;
    private final DocumentService documentService;
    private final EmbeddingService embeddingService;
    private final RetrievalService retrievalService;
    private final RagService ragService;

    public DocumentController(PdfService pdfService, DocumentService documentService, EmbeddingService embeddingService, RetrievalService retrievalService, RagService ragService) {
        this.pdfService = pdfService;
        this.documentService = documentService;
        this.embeddingService = embeddingService;
        this.retrievalService = retrievalService;
        this.ragService = ragService;
    }

    @PostMapping("/extract")
    public ResponseEntity<String> extractText(
            @RequestParam("file") MultipartFile file) throws IOException {

        String text = pdfService.extractText(file);
        return ResponseEntity.ok(text);

    }

    @PostMapping("/chunk")
    public ResponseEntity<List<String>> chunk (
            @RequestParam("file") MultipartFile file) throws IOException {

        List<TextSegment> chunks = pdfService.extractAndChunk(file);

        List<String> result = chunks.stream()
                .map(TextSegment::text)
                .toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload (
            @RequestParam("file") MultipartFile file,
            @RequestParam("knowledgeSpaceId") Long knowledgeSpaceId)
        throws IOException {

        Document document = documentService.processDocument(file, knowledgeSpaceId);

        return ResponseEntity.ok(
                "Document processed successfully. ID: " + document.getId()
        );
    }

    @PostMapping("/embedding-test")
    public ResponseEntity<String> embeddingTest(
            @RequestBody String text) {

        float[] embedding = embeddingService.generateEmbedding(text);

        return ResponseEntity.ok("Embedding dimensions: " + embedding.length);
    }

    @GetMapping("/retrieve")
    public List<RetrievedChunkResponse> retrieve(
            @RequestParam String question,
            @RequestParam Long knowledgeSpaceId) {

        return retrievalService.retrieve(question, knowledgeSpaceId, 5);
    }

    @GetMapping("/rag")
    public RagResponse rag(@RequestParam String question, @RequestParam Long knowledgeSpaceId) {
        return ragService.answer(question, knowledgeSpaceId, "");
    }

}
