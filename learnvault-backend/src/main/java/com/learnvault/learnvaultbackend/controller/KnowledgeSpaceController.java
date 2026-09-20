package com.learnvault.learnvaultbackend.controller;

import com.learnvault.learnvaultbackend.model.KnowledgeSpace;
import com.learnvault.learnvaultbackend.service.KnowledgeSpaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
