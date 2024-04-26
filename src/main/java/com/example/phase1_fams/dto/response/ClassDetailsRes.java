package com.example.phase1_fams.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.example.phase1_fams.dto.AttendeeDTO;
import com.example.phase1_fams.dto.ClassGeneralDTO;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetailsRes {
    private Long classId;
    private String classCode;
    private String className;
    private String status;
    private ClassGeneralDTO classGeneralDTO;
    private List<LocalDate> calendarDates = new ArrayList<>();
    private AttendeeDTO attendeeDTO;
    private TrainingProgramRes trainingProgramRes;
    private String createdBy;
    private LocalDate createdDate;
    private String modifiedBy;
    private LocalDate modifiedDate;
}
