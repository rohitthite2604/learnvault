package com.learnvault.learnvaultbackend.repository;

import com.learnvault.learnvaultbackend.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    @Query(value = """
        SELECT dc.*
        FROM document_chunks dc
        JOIN documents d
            ON dc.document_id = d.id
        WHERE d.knowledge_space_id = :knowledgeSpaceId
          AND dc.embedding IS NOT NULL
        ORDER BY dc.embedding <=> CAST(:embedding AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<DocumentChunk> findSimilarChunks(
            @Param("embedding") String embedding,
            @Param("knowledgeSpaceId") Long knowledgeSpaceId,
            @Param("topK") int topK
    );
}
