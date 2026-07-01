package com.noteflow.dto;

import com.noteflow.entity.Todo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TodoDto {
    private Long id;
    private String title;
    private String description;
    private Boolean isCompleted;
    private String priority;
    private LocalDateTime dueDate;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userName;

    public static TodoDto from(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setIsCompleted(todo.getIsCompleted());
        dto.setPriority(todo.getPriority().name());
        dto.setDueDate(todo.getDueDate());
        dto.setCategory(todo.getCategory());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        if (todo.getUser() != null) {
            dto.setUserId(todo.getUser().getId());
            dto.setUserName(todo.getUser().getName());
        }
        return dto;
    }

    @Data
    public static class CreateRequest {
        private String title;
        private String description;
        private String priority = "MEDIUM";
        private LocalDateTime dueDate;
        private String category;
    }
}
