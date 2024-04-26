package com.example.phase1_fams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActiveSyllabus {
    String code;
    String name;
    String version;
    int duration;
    Float totalTime;
    int status;
    String createdBy;
    LocalDate createdDate;
    String modifiedBy;
    LocalDate modifiedDate;
}
