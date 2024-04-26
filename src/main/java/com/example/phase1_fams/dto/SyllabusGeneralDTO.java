package com.example.phase1_fams.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusGeneralDTO {
    private String level;
    private int attendeeNumber;
    private String technicalRequirements;
    private String courseObjectives;
}
