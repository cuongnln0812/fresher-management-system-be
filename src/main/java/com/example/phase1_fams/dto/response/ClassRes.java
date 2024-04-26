package com.example.phase1_fams.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.example.phase1_fams.dto.AttendeeDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassRes {

    private Long id;

    private String code;

    private String status;

    private String name;

    private String location;

    private String fsu;

    private LocalTime startTime;

    private LocalTime endTime;

    private String createdBy;

    private LocalDate createdDate;

    private String modifiedBy;

    private LocalDate modifiedDate;

    private TrainingProgramRes trainingProgram;

    private AttendeeDTO attendeeDTO;

    private Set<ClassUserRes> classUserDTOSet = new HashSet<>();

    private Set<SessionRes> sessionSet = new HashSet<>();

}
