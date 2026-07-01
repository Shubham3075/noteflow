package com.noteflow.service;

import com.noteflow.dto.NoteDto;
import com.noteflow.entity.Note;
import com.noteflow.entity.User;
import com.noteflow.repository.NoteRepository;
import com.noteflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired private NoteRepository noteRepository;
    @Autowired private UserRepository userRepository;

    public List<NoteDto> getUserNotes(Long userId) {
        return noteRepository
                .findByUserIdAndIsArchivedFalseOrderByIsPinnedDescCreatedAtDesc(userId)
                .stream().map(NoteDto::from).collect(Collectors.toList());
    }

    public List<NoteDto> getArchivedNotes(Long userId) {
        return noteRepository
                .findByUserIdAndIsArchivedTrueOrderByCreatedAtDesc(userId)
                .stream().map(NoteDto::from).collect(Collectors.toList());
    }

    @Transactional
    public NoteDto createNote(Long userId, NoteDto.CreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            request.setTitle("Untitled");
        }

        Note.NoteType noteType = Note.NoteType.NOTE;
        if (request.getType() != null) {
            try { noteType = Note.NoteType.valueOf(request.getType()); }
            catch (Exception ignored) {}
        }

        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .color(request.getColor() != null ? request.getColor() : "#ffffff")
                .type(noteType)
                .isPinned(false)
                .isArchived(false)
                .user(user)
                .build();

        return NoteDto.from(noteRepository.save(note));
    }

    @Transactional
    public NoteDto updateNote(Long noteId, Long userId, NoteDto.CreateRequest request) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(userId))
            throw new RuntimeException("Access denied");

        if (request.getTitle() != null) note.setTitle(request.getTitle());
        if (request.getContent() != null) note.setContent(request.getContent());
        if (request.getColor() != null) note.setColor(request.getColor());
        return NoteDto.from(noteRepository.save(note));
    }

    @Transactional
    public NoteDto togglePin(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(userId))
            throw new RuntimeException("Access denied");
        note.setIsPinned(!note.getIsPinned());
        return NoteDto.from(noteRepository.save(note));
    }

    @Transactional
    public NoteDto toggleArchive(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(userId))
            throw new RuntimeException("Access denied");
        note.setIsArchived(!note.getIsArchived());
        return NoteDto.from(noteRepository.save(note));
    }

    @Transactional
    public void deleteNote(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(userId))
            throw new RuntimeException("Access denied");
        noteRepository.delete(note);
    }

    public List<NoteDto> getAllNotesAdmin() {
        return noteRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(NoteDto::from).collect(Collectors.toList());
    }
}
