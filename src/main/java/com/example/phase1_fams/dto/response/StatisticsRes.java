package com.example.phase1_fams.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsRes {
    private Integer totalNumberOfUser;
    private Integer totalNumberOfUserActive;
    private Integer totalNumberOfUserInactive;

    private Integer totalNumberOfSyllabus;
    private Integer totalNumberOfSyllabusActive;
    private Integer totalNumberOfSyllabusDraft;
    private Integer totalNumberOfSyllabusInactive;

    private Integer totalNumberOfTrainingProgram;
    private Integer totalNumberOfTrainingProgramActive;
    private Integer totalNumberOfTrainingProgramDraft;
    private Integer totalNumberOfTrainingProgramInactive;

    private Integer totalNumberOfClass;
    private Integer totalNumberOfClassPlanning;
    private Integer totalNumberOfClassScheduled;
    private Integer totalNumberOfClassOpening;
    private Integer totalNumberOfClassClosed;
    private Integer totalNumberOfClassInactive;
}
