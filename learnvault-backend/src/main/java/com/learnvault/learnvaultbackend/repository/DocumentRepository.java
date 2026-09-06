package com.learnvault.learnvaultbackend.repository;

import com.learnvault.learnvaultbackend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
