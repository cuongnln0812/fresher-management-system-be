package com.example.phase1_fams.model;

import com.example.phase1_fams.dto.exception.ApiException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Length;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_syllabus")
public class Syllabus {
    @Id
    @Column(name = "topic_code")
    private String code;
    @Column(name = "topic_name")
    private String name;
    private String level;
    private String version;
    private int i1;
    private int i2;
    private int i3;
    @Column(name = "total_time")
    private Float totalTime;
    @Column(name = "attendee_number")
    private int attendeeNumber;
    @Column(name = "technical_requirements", length = Length.LOB_DEFAULT)
    private String technicalRequirements;
    @Column(name = "course_objectives", length = Length.LOB_DEFAULT)
    private String courseObjectives;
    @Column(name = "quiz_assessment")
    private int quizAssessment;
    @Column(name = "assignment_assessment")
    private int assignmentAssessment;
    @Column(name = "final_assessment")
    private int finalAssessment;
    @Column(name = "final_theory_assessment")
    private int finalTheoryAssessment;
    @Column(name = "final_practice_assessment")
    private int finalPracticeAssessment;
    @Column(name = "gpa_criteria")
    private int gpaCriteria;
    @Column(name = "training_principle", length = Length.LOB_DEFAULT)
    private String trainingPrinciple;
    @Column(name = "retest_principle", length = Length.LOB_DEFAULT)
    private String reTestPrinciple;
    @Column(name = "marking_principle", length = Length.LOB_DEFAULT)
    private String markingPrinciple;
    @Column(name = "waiver_criteria_principle", length = Length.LOB_DEFAULT)
    private String waiverCriteriaPrinciple;
    @Column(name = "others_principle", length = Length.LOB_DEFAULT)
    private String othersPrinciple;
    private int status;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private LocalDate createdDate;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    public void updateVersionActive(){
        i2++;
        i3 = 0;
    }

    public void updateVersionDraft(){
        i3++;
    }

    public void setVersion(int i1){
        if(i1 < this.i1) throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid version number");
        if(i1 > this.i1){
            this.i1 = i1;
            this.i2 = 0;
            this.i3 = 0;
            this.version = this.i1 + "." + i2 + "." + i3;
        }
        if(i1 == 0){
            this.version = i1 + "." + i2 + "." + i3;
        }
    }

    public String getVersion(){
        return this.i1 + "." + i2 + "." + i3;
    }

    public void setCreatedDate() {
        this.createdDate = LocalDate.now();
    }

    public void setModifiedDate() {
        Instant instant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        this.modifiedDate = LocalDate.now();
    }

    public void addDayUnit(DaysUnit daysUnit) {
        if (daysUnit != null) {
            daysUnits.add(daysUnit);
            daysUnit.setSyllabus(this);
        }
    }

    @OneToMany(mappedBy = "syllabus")
    private Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();

    @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<DaysUnit> daysUnits = new HashSet<>();

}
