package com.example.phase1_fams.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.phase1_fams.model.TrainingProgramSyllabus;
import com.example.phase1_fams.model.TrainingProgramSyllabusKey;

import jakarta.transaction.Transactional;

public interface TrainingProgramSyllabusRepository
        extends JpaRepository<TrainingProgramSyllabus, TrainingProgramSyllabusKey> {

    @Modifying
    @Query("SELECT t FROM TrainingProgramSyllabus t WHERE t.id.trainingProgramId = :id")
    Set<TrainingProgramSyllabus> findByTrainingProgramId(Long id);

    @Transactional
    void deleteBySyllabusCode(String syllabusCode);

}
