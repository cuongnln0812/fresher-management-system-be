package com.example.phase1_fams.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClassUser {
    @EmbeddedId
    ClassUserKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    Users users;

    @ManyToOne
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    Class aClass;

    @Column(name = "user_type")
    private String userType;
}
