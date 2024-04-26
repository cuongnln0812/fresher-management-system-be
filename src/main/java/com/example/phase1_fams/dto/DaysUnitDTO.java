package com.example.phase1_fams.dto;

import lombok.*;

import java.util.Set;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DaysUnitDTO {
    private int dayNumber;
    private Set<TrainingUnitDTO> trainingUnits;
}
