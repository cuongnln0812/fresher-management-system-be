package com.example.phase1_fams.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusOutlineDTO {
    private Set<DaysUnitDTO> days;
}
