package com.example.phase1_fams.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusGeneralRes {
    private String level;
    private int attendeeNumber;
    private List<String> totalOutputStandards;
    private String technicalRequirements;
    private String courseObjectives;
}
