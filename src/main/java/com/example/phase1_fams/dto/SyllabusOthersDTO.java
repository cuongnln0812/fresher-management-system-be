package com.example.phase1_fams.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusOthersDTO {

    private AssessmentSchemeDTO assessmentScheme = new AssessmentSchemeDTO();

    private TrainingPrincipleDTO trainingDeliveryPrinciple = new TrainingPrincipleDTO();
}
