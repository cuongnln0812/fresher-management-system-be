package com.example.phase1_fams.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DaysUnitRes {
    @Schema(description = "The unique identifier of the entity. Null if the entity is new and not yet persisted.", nullable = true)
    private Long id;
    private int dayNumber;
    private List<TrainingUnitRes> trainingUnits = new ArrayList<>();

    public void addTraining(TrainingUnitRes trainingUnitRes) {
        if (trainingUnitRes != null) trainingUnits.add(trainingUnitRes);
    }
}
