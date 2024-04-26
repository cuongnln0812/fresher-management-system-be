package com.example.phase1_fams.dto.request;

import com.example.phase1_fams.dto.TrainingProgramSyllabusDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramReq {
    @NotBlank(message = "Training program name must not be blank")
    private String name;

    private int duration;

    private String description;

    private Set<TrainingProgramSyllabusDTO> trainingProgramDTOSet;

}
