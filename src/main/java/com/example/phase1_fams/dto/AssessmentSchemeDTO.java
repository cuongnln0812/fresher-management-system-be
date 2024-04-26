package com.example.phase1_fams.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSchemeDTO {
    private int quiz;

    private int assignment;

    @JsonProperty("final")
    private int _final;

    private int finalTheory;

    private int finalPractice;

    private int gpa;
}
