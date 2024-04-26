package com.example.phase1_fams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyllabusPageRes {
    private String syllabusName;
    private String code;
    private LocalDate createdDate;
    private String createdBy;
    private List<String> outputStandard;
    private int duration;
    private int status;
}
