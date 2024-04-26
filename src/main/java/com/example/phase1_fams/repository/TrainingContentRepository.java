package com.example.phase1_fams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.phase1_fams.model.TrainingContent;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingContentRepository extends JpaRepository<TrainingContent, Long> {

}