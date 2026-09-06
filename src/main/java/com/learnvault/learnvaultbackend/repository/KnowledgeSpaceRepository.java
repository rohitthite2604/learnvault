package com.learnvault.learnvaultbackend.repository;

import com.learnvault.learnvaultbackend.model.KnowledgeSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeSpaceRepository extends JpaRepository<KnowledgeSpace, Long> {
}
