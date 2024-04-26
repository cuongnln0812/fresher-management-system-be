package com.example.phase1_fams.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingPrincipleDTO {
    private String training;
    private String retest;
    private String marking;
    private String waiverCriteria;
    private String others;
}
