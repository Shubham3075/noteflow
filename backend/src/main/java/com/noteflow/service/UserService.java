package com.noteflow.service;

import com.noteflow.dto.UserDto;
import com.noteflow.entity.User;
import com.noteflow.repository.NoteRepository;
import com.noteflow.repository.TodoRepository;
import com.noteflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto dto = UserDto.from(user);
        dto.setNotesCount(noteRepository.countByUserId(userId));
        dto.setTodosCount(todoRepository.countByUserIdAndIsCompletedFalse(userId));
        return dto;
    }

    public UserDto updateProfile(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (name != null && !name.isBlank()) user.setName(name);
        return UserDto.from(userRepository.save(user));
    }

    public UserDto uploadProfilePhoto(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Path uploadPath = Paths.get(uploadDir + "profiles/");
        Files.createDirectories(uploadPath);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setProfilePhoto("/uploads/profiles/" + filename);
        return UserDto.from(userRepository.save(user));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(user -> {
                    UserDto dto = UserDto.from(user);
                    dto.setNotesCount(noteRepository.countByUserId(user.getId()));
                    dto.setTodosCount(todoRepository.countByUserIdAndIsCompletedFalse(user.getId()));
                    return dto;
                }).collect(Collectors.toList());
    }

    public UserDto toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(!user.getIsActive());
        return UserDto.from(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
