package com.example.phase1_fams.dto.request;


import com.example.phase1_fams.dto.SyllabusOthersDTO;
import com.example.phase1_fams.dto.SyllabusGeneralDTO;
import com.example.phase1_fams.dto.response.SyllabusOutlineRes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusReqUpdate {
    @NotBlank(message = "Syllabus code must not be blank")
    private String code;
    @NotBlank(message = "Syllabus name must not be blank")
    private String syllabusName;
    @NotNull(message = "Version must not be null")
    private int version;
    private SyllabusGeneralDTO syllabusGeneral;
    private SyllabusOutlineRes syllabusOutline;
    private SyllabusOthersDTO syllabusOthers;

    List<Long> deletedDaysId = new ArrayList<>();
    List<Long> deletedTrainingUnitsId = new ArrayList<>();
    List<Long> deletedTrainingContentsId = new ArrayList<>();
}
