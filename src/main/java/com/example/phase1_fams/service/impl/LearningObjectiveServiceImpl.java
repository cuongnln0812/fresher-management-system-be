package com.example.phase1_fams.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.phase1_fams.repository.LearningObjectiveRepository;
import com.example.phase1_fams.service.LearningObjectiveService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LearningObjectiveServiceImpl implements LearningObjectiveService {

    private final LearningObjectiveRepository repository;

    @Autowired
    public LearningObjectiveServiceImpl(LearningObjectiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> getAllOutputCode() {
        return repository.getAllOutputCode();
    }
}
