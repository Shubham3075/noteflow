package com.noteflow.repository;

import com.noteflow.entity.OtpStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpStore, Long> {
    Optional<OtpStore> findTopByEmailAndIsUsedFalseOrderByCreatedAtDesc(String email);
    void deleteByEmail(String email);
}
