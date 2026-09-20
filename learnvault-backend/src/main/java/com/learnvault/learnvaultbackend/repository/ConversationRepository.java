package com.learnvault.learnvaultbackend.repository;

import com.learnvault.learnvaultbackend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByKnowledgeSpaceIdOrderByUpdatedAtDesc(Long knowledgeSpaceId);
}
