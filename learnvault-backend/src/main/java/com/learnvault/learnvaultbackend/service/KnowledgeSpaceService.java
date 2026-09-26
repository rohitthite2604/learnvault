package com.learnvault.learnvaultbackend.service;

import com.learnvault.learnvaultbackend.dto.KnowledgeSpaceRequest;
import com.learnvault.learnvaultbackend.exception.BadRequestException;
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

    public KnowledgeSpace createKnowledgeSpace(KnowledgeSpaceRequest request) {

        String name = request.getName().trim();

        if (knowledgeSpaceRepository.existsByName(name)) {
            throw new BadRequestException("A Knowledge Space with this name already exists");
        }

        KnowledgeSpace knowledgeSpace = KnowledgeSpace.builder()
                .name(name)
                .description(request.getDescription() == null
                        ? null
                        : request.getDescription().trim()
                ).build();

        return knowledgeSpaceRepository.save(knowledgeSpace);
    }
}
