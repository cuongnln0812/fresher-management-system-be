package com.example.phase1_fams.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface LearningObjectiveService {
    List<String> getAllOutputCode();
}
