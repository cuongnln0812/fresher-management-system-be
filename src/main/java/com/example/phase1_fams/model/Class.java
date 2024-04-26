package com.example.phase1_fams.model;

import java.time.*;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;
    @Column(name = "class_code", unique = true)
    private String code;
    @Column(name = "class_name")
    private String name;
    private int duration;
    private String location;
    private String fsu;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "class_time")
    private String classTime;
    private String status;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private LocalDate createdDate;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "modified_date")
    private LocalDate modifiedDate;
    @Column(name = "attendee_type")
    private String attendeeType;
    @Column(name = "planned_attendee")
    private int plannedAttendee;
    @Column(name = "accepted_attendee")
    private int acceptedAttendee;
    @Column(name = "actual_attendee")
    private int actualAttendee;

    public void setCreatedDate() {
        this.createdDate = LocalDate.now();
    }

    public void setModifiedDate() {
        this.modifiedDate = LocalDate.now();
    }

    @ManyToOne
    @JoinColumn(name = "training_program_id")
    private TrainingProgram trainingProgram;

    @OneToMany(mappedBy = "aClass", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<ClassUser> classUserType;

    @OneToMany(mappedBy = "aClass",cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<Session> sessions;

}
