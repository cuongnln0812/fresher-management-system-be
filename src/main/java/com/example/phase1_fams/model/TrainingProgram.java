package com.example.phase1_fams.model;

import java.time.LocalDate;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Length;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "training_program")
public class TrainingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_program_id")
    private Long id;
    @Column(name = "training_program_name")
    private String name;
    @Column(length = Length.LOB_DEFAULT)
    private String description;
    private int duration;
    private int status;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private LocalDate createdDate;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    public void setCreatedDate() {
        this.createdDate = LocalDate.now();
    }

    public void setModifiedDate() {
        this.modifiedDate = LocalDate.now();
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private Users users;

    @OneToMany(mappedBy = "trainingProgram", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Class> classes = new HashSet<>();

    @OneToMany(mappedBy = "trainingProgram", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<TrainingProgramSyllabus> trainingProgramSyllabusSet;
}
