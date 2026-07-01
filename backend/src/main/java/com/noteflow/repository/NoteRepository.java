package com.noteflow.repository;

import com.noteflow.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserIdAndIsArchivedFalseOrderByIsPinnedDescCreatedAtDesc(Long userId);
    List<Note> findByUserIdAndIsArchivedTrueOrderByCreatedAtDesc(Long userId);
    List<Note> findAllByOrderByCreatedAtDesc();
    long countByUserId(Long userId);
}
