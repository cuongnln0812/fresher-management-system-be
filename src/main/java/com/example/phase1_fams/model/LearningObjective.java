package com.example.phase1_fams.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
@Table(name = "learning_objective")
public class LearningObjective {
    @Id
    @Column(name = "objective_code", insertable = false, updatable = false)
    private String code;
    @Column(name = "objective_name")
    private String name;
    private String type;
    private String description;
    @ManyToMany(mappedBy = "objectiveCodes")
    Set<TrainingContent> contents;
}
