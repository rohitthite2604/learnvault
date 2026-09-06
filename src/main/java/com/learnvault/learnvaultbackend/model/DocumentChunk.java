package com.learnvault.learnvaultbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "document_chunks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer pageNumber;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(768)")
    private float[] embedding;
}
