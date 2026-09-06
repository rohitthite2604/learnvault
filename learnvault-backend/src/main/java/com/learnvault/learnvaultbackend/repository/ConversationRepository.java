package com.learnvault.learnvaultbackend.repository;

import com.learnvault.learnvaultbackend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
