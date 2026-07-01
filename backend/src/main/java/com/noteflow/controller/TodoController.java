package com.noteflow.controller;

import com.noteflow.dto.TodoDto;
import com.noteflow.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    private Long getUserId(Authentication auth) {
        return (Long) auth.getCredentials();
    }

    @GetMapping
    public ResponseEntity<?> getTodos(Authentication auth) {
        return ResponseEntity.ok(todoService.getUserTodos(getUserId(auth)));
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoDto.CreateRequest request, Authentication auth) {
        try {
            return ResponseEntity.ok(todoService.createTodo(getUserId(auth), request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id, @RequestBody TodoDto.CreateRequest request, Authentication auth) {
        try {
            return ResponseEntity.ok(todoService.updateTodo(id, getUserId(auth), request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleComplete(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(todoService.toggleComplete(id, getUserId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id, Authentication auth) {
        todoService.deleteTodo(id, getUserId(auth));
        return ResponseEntity.ok(Map.of("message", "Todo deleted"));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodosAdmin());
    }
}
