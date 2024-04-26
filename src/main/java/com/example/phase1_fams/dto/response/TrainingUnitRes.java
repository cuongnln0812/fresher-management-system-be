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
public class TrainingUnitRes {
    @Schema(description = "The unique identifier of the entity. Null if the entity is new and not yet persisted.", nullable = true)
    private Long id;
    private int unitNumber;
    private String unitName;
    private Float trainingTime;
    private List<TrainingContentRes> trainingContents = new ArrayList<>();

    public void addContents(TrainingContentRes contentRes){
        if(contentRes != null) trainingContents.add(contentRes);
    }
}
