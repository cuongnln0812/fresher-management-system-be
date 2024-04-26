package com.example.phase1_fams.dto.response;

import com.example.phase1_fams.dto.SyllabusOthersDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@NoArgsConstructor
@Getter
@Setter
public class SyllabusDetailsRes {
    private String code;
    private String syllabusName;
    private String version;
    private int duration;
    private Float totalTimes;
    private SyllabusGeneralRes syllabusGeneral = new SyllabusGeneralRes();
    private SyllabusOutlineRes syllabusOutline = new SyllabusOutlineRes();
    private SyllabusOthersDTO syllabusOthers = new SyllabusOthersDTO();
    private String createdBy;
    private LocalDate createdDate;
    private String modifiedBy;
    private LocalDate modifiedDate;
    private int status;
}
