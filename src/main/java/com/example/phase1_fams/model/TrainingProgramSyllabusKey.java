package com.example.phase1_fams.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainingProgramSyllabusKey implements Serializable {
    @Column(name = "topic_code")
    private String topicCode;

    @Column(name = "training_program_id")
    private Long trainingProgramId;
}
