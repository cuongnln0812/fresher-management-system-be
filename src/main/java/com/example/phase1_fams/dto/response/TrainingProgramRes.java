package com.example.phase1_fams.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgramRes {
    private Long id;
    private String trainingProgramName;
    private String generalInformation;
    private int duration;
    private int status;
    private List<SyllabusContent> syllabusContents = new ArrayList<>();
    private String createdBy;
    private LocalDate createdDate;
    private String modifiedBy;
    private LocalDate modifiedDate;
}
