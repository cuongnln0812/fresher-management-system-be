package com.example.phase1_fams.dto;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingUnitDTO {
    private int unitNumber;
    private String unitName;
    private Float trainingTime;
    private Set<TrainingContentDTO> trainingContents;
}
