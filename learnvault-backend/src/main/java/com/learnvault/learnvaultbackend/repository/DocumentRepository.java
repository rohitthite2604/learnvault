package com.learnvault.learnvaultbackend.repository;

import com.learnvault.learnvaultbackend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByKnowledgeSpaceId(Long knowledgeSpaceId);
}
