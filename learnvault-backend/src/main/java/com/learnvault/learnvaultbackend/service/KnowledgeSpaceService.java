package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.model.KnowledgeSpace;
import com.learnvault.learnvaultbackend.repository.KnowledgeSpaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeSpaceService {

    private final KnowledgeSpaceRepository knowledgeSpaceRepository;

    public KnowledgeSpaceService(KnowledgeSpaceRepository knowledgeSpaceRepository) {
        this.knowledgeSpaceRepository = knowledgeSpaceRepository;
    }

    public List<KnowledgeSpace> getAllKnowledgeSpaces() {
        return knowledgeSpaceRepository.findAll();
    }
}
