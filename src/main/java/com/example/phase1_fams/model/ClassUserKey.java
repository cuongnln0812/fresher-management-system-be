package com.example.phase1_fams.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClassUserKey implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "class_id")
    private Long classId;
}
