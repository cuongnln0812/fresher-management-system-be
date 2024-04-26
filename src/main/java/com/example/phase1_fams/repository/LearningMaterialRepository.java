package com.example.phase1_fams.repository;

import com.example.phase1_fams.model.LearningMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningMaterialRepository extends JpaRepository<LearningMaterial, Long> {
}
