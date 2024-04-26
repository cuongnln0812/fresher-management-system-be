package com.example.phase1_fams.dto;

import java.time.LocalDate;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingProgramDTO {

    private Long id;

    private String trainingProgramName;

//    private Set<String> syllabusCodes = new HashSet<>();

    private int duration;

    private int status;

    private String createdBy;

    private LocalDate createdDate;

    private String modifiedBy;

    private LocalDate modifiedDate;
}
