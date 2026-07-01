package com.noteflow.dto;

import com.noteflow.entity.Note;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private String color;
    private Boolean isPinned;
    private Boolean isArchived;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userName;

    public static NoteDto from(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setColor(note.getColor());
        dto.setIsPinned(note.getIsPinned());
        dto.setIsArchived(note.getIsArchived());
        dto.setType(note.getType().name());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        if (note.getUser() != null) {
            dto.setUserId(note.getUser().getId());
            dto.setUserName(note.getUser().getName());
        }
        return dto;
    }

    @Data
    public static class CreateRequest {
        private String title;
        private String content;
        private String color;
        private String type = "NOTE";
    }
}
