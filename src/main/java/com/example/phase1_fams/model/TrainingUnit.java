package com.example.phase1_fams.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "training_unit")
public class TrainingUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Long id;
    @Column(name = "unit_number")
    private int unitNumber;
    @Column(name = "unit_name")
    private String unitName;
    @Column(name = "training_time")
    private Float trainingTime;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private DaysUnit daysUnit;

    @OneToMany(mappedBy = "trainingUnit", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<TrainingContent> trainingContents = new HashSet<>();

    public void addTrainingContent(TrainingContent trainingContent) {
        if (trainingContent != null) {
            trainingContents.add(trainingContent);
            trainingContent.setTrainingUnit(this);
        }
    }

}
