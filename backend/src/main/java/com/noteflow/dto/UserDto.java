package com.noteflow.dto;

import com.noteflow.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String profilePhoto;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private long notesCount;
    private long todosCount;

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setProfilePhoto(user.getProfilePhoto());
        dto.setRole(user.getRole().name());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
