package com.example.phase1_fams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.phase1_fams.model.TrainingUnit;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingUnitRepository extends JpaRepository<TrainingUnit, Long> {
}
