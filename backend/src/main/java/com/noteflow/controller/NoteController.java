package com.noteflow.controller;

import com.noteflow.dto.NoteDto;
import com.noteflow.security.JwtUtils;
import com.noteflow.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private JwtUtils jwtUtils;

    private Long getUserId(Authentication auth) {
        return (Long) auth.getCredentials();
    }

    @GetMapping
    public ResponseEntity<?> getNotes(Authentication auth) {
        return ResponseEntity.ok(noteService.getUserNotes(getUserId(auth)));
    }

    @GetMapping("/archived")
    public ResponseEntity<?> getArchivedNotes(Authentication auth) {
        return ResponseEntity.ok(noteService.getArchivedNotes(getUserId(auth)));
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody NoteDto.CreateRequest request, Authentication auth) {
        try {
            return ResponseEntity.ok(noteService.createNote(getUserId(auth), request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody NoteDto.CreateRequest request, Authentication auth) {
        try {
            return ResponseEntity.ok(noteService.updateNote(id, getUserId(auth), request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<?> togglePin(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(noteService.togglePin(id, getUserId(auth)));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<?> toggleArchive(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(noteService.toggleArchive(id, getUserId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, Authentication auth) {
        noteService.deleteNote(id, getUserId(auth));
        return ResponseEntity.ok(Map.of("message", "Note deleted"));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotesAdmin());
    }
}
