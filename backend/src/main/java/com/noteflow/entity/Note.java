package com.noteflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    // FIX: PostgreSQL uses TEXT not NVARCHAR(MAX)
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 20)
    private String color;

    @Column(nullable = false)
    private Boolean isPinned = false;

    @Column(nullable = false)
    private Boolean isArchived = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private NoteType type = NoteType.NOTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isPinned == null) isPinned = false;
        if (isArchived == null) isArchived = false;
        if (type == null) type = NoteType.NOTE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum NoteType {
        NOTE, CHECKLIST, REMINDER
    }
}
