package com.example.phase1_fams.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaysUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long id;
    @Column(name = "day_number")
    private int dayNumber;

    @OneToMany(mappedBy = "daysUnit", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<TrainingUnit> trainingUnits = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "topic_code")
    private Syllabus syllabus;

    public void addTrainingUnit(TrainingUnit trainingUnit) {
        if (trainingUnit != null) {
            trainingUnits.add(trainingUnit);
            trainingUnit.setDaysUnit(this);
        }
    }
}
