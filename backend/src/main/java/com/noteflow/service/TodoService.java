package com.noteflow.service;

import com.noteflow.dto.TodoDto;
import com.noteflow.entity.Todo;
import com.noteflow.entity.User;
import com.noteflow.repository.TodoRepository;
import com.noteflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    @Autowired private TodoRepository todoRepository;
    @Autowired private UserRepository userRepository;

    public List<TodoDto> getUserTodos(Long userId) {
        return todoRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(TodoDto::from).collect(Collectors.toList());
    }

    @Transactional
    public TodoDto createTodo(Long userId, TodoDto.CreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new RuntimeException("Title is required");

        Todo.Priority priority = Todo.Priority.MEDIUM;
        if (request.getPriority() != null) {
            try { priority = Todo.Priority.valueOf(request.getPriority()); }
            catch (Exception ignored) {}
        }

        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(priority)
                .dueDate(request.getDueDate())
                .category(request.getCategory())
                .isCompleted(false)
                .user(user)
                .build();

        return TodoDto.from(todoRepository.save(todo));
    }

    @Transactional
    public TodoDto updateTodo(Long todoId, Long userId, TodoDto.CreateRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        if (!todo.getUser().getId().equals(userId))
            throw new RuntimeException("Access denied");

        if (request.getTitle() != null) todo.setTitle(request.getTitle());
        if (request.getDescription() != null) todo.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            try { todo.setPriority(Todo.Priority.valueOf(request.getPriority())); }
            catch (Exception ignored) {}
        }
        if (request.getDueDate() != null) todo.setDueDate(request.getDueDate());
        if (request.getCategory() != null) todo.setCategory(request.getCategory());

        return TodoDto.from(todoRepository.save(todo));
    }

    @Transactional
    public TodoDto toggleComplete(Long todoId, Long userId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        if (!todo.getUser().getId().equals(userId))
            throw new RuntimeException("Access denied");
        todo.setIsCompleted(!todo.getIsCompleted());
        return TodoDto.from(todoRepository.save(todo));
    }

    @Transactional
    public void deleteTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        if (!todo.getUser().getId().equals(userId))
            throw new RuntimeException("Access denied");
        todoRepository.delete(todo);
    }

    public List<TodoDto> getAllTodosAdmin() {
        return todoRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(TodoDto::from).collect(Collectors.toList());
    }
}
