package com.example.phase1_fams.controller;

import com.example.phase1_fams.service.LearningObjectiveService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/output-standard")
@SecurityRequirement(name = "bearerAuth")
public class LearningObjectiveController {

    private final LearningObjectiveService learningObjectiveService;

    @Autowired
    public LearningObjectiveController(LearningObjectiveService learningObjectiveService) {
        this.learningObjectiveService = learningObjectiveService;
    }

    @GetMapping()
    public ResponseEntity<?> getOutputStandardCodeList(){
        return new ResponseEntity<>(learningObjectiveService.getAllOutputCode(), HttpStatus.OK);
    }
}
