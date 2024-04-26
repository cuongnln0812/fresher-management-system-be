package com.example.phase1_fams.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.phase1_fams.model.LearningObjective;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningObjectiveRepository extends JpaRepository<LearningObjective, String> {
    Optional<LearningObjective> findByCode(String code);

    @Query("SELECT l.code FROM LearningObjective l")
    List<String> getAllOutputCode();
}
