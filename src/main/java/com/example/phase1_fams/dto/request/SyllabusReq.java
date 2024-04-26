package com.example.phase1_fams.dto.request;


import com.example.phase1_fams.dto.SyllabusOthersDTO;
import com.example.phase1_fams.dto.SyllabusOutlineDTO;
import com.example.phase1_fams.dto.SyllabusGeneralDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusReq {
    @NotBlank(message = "Syllabus code must not be blank")
    private String code;
    @NotBlank(message = "Syllabus name must not be blank")
    private String syllabusName;
    private SyllabusGeneralDTO syllabusGeneral;
    private SyllabusOutlineDTO syllabusOutline;
    private SyllabusOthersDTO syllabusOthers;
}
