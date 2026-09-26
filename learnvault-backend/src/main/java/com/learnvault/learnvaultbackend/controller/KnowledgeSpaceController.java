package com.learnvault.learnvaultbackend.controller;

import com.learnvault.learnvaultbackend.dto.KnowledgeSpaceRequest;
import com.learnvault.learnvaultbackend.model.KnowledgeSpace;
import com.learnvault.learnvaultbackend.service.KnowledgeSpaceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-spaces")
public class KnowledgeSpaceController {

    private final KnowledgeSpaceService knowledgeSpaceService;

    public KnowledgeSpaceController(KnowledgeSpaceService knowledgeSpaceService) {
        this.knowledgeSpaceService = knowledgeSpaceService;
    }

    @GetMapping
    public List<KnowledgeSpace> getAllKnowledgeSpaces() {
        return knowledgeSpaceService.getAllKnowledgeSpaces();
    }

    @PostMapping
    public KnowledgeSpace createKnowledgeSpace(@Valid @RequestBody KnowledgeSpaceRequest request) {
        return knowledgeSpaceService.createKnowledgeSpace(request);
    }
}
