package com.example.phase1_fams.dto.response;

import com.example.phase1_fams.dto.LearningMaterialDto;
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
public class TrainingContentRes {
    @Schema(description = "The unique identifier of the entity. Null if the entity is new and not yet persisted.", nullable = true)
    private Long id;
    private int orderNumber;
    private String contentName;
    private int duration;
    private List<String> outputStandards;
    private String deliveryType;
    private String method;
    private List<LearningMaterialDto> learningMaterials = new ArrayList<>();

    public void addMaterials(LearningMaterialDto material){
        if(material != null) learningMaterials.add(material);
    }
}
