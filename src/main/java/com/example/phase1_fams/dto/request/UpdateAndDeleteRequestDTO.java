package com.example.phase1_fams.dto.request;

import com.example.phase1_fams.dto.LearningMaterialDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAndDeleteRequestDTO {
    private Long id;
    private LearningMaterialDto learningMaterialDto;
    private boolean isDeleteOperation;
}
