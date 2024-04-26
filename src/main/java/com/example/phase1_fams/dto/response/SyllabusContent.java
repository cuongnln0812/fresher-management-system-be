package com.example.phase1_fams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SyllabusContent {
    private int sequence;
    private String syllabusName;
    private String syllabusCode;
    private String syllabusVersion;
    private Float totalTime;
    private int totalDays;
    private int status;
    private List<DaysUnitRes> syllabusDetails = new ArrayList<>();
    private LocalDate createdDate;
    private String createdBy;
    private LocalDate modifiedDate;
    private String modifiedBy;
}
