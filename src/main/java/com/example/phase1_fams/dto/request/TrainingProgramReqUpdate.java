package com.example.phase1_fams.dto.request;

import java.util.Set;

import com.example.phase1_fams.dto.TrainingProgramSyllabusDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramReqUpdate {
    @NotNull(message = "Training Program id must not be null")
    private Long id;
    @NotBlank(message = "Training program name must not be blank")
    private String name;
    private Integer duration;
    private String description;
    private Set<TrainingProgramSyllabusDTO> trainingProgramDTOSet;
}
