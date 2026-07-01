package com.noteflow.repository;

import com.noteflow.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Todo> findByUserIdAndIsCompletedFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsCompletedFalse(Long userId);
    List<Todo> findAllByOrderByCreatedAtDesc();
}
