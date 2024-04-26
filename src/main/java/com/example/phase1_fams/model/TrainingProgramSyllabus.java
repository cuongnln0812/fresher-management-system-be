package com.example.phase1_fams.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name = "training_program_syllabus")
public class TrainingProgramSyllabus {
    @EmbeddedId
    TrainingProgramSyllabusKey id;

    @ManyToOne
    @MapsId("topicCode")
    @JoinColumn(name = "topic_code")
    Syllabus syllabus;

    @ManyToOne
    @MapsId("trainingProgramId")
    @JoinColumn(name = "training_program_id", insertable = false, updatable = false)
    TrainingProgram trainingProgram;

    int sequence;

}
