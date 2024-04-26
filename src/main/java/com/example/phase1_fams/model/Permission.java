package com.example.phase1_fams.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    USER_READ("user:read"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),

    SYLLABUS_READ("syllabus:read"),
    SYLLABUS_CREATE("syllabus:create"),
    SYLLABUS_UPDATE("syllabus:update"),
    SYLLABUS_DELETE("syllabus:delete"),
    SYLLABUS_IMPORT("syllabus:import"),

    TRAINING_PROGRAM_READ("training_program:read"),
    TRAINING_PROGRAM_CREATE("training_program:create"),
    TRAINING_PROGRAM_UPDATE("training_program:update"),
    TRAINING_PROGRAM_DELETE("training_program:delete"),
    TRAINING_PROGRAM_IMPORT("training_program:import"),

    CLASS_READ("class:read"),
    CLASS_CREATE("class:create"),
    CLASS_UPDATE("class:update"),
    CLASS_DELETE("class:delete"),

    LEARNING_MATERIAL_READ("learning_material:read"),
    LEARNING_MATERIAL_UPLOAD("learning_material:upload"),
    LEARNING_MATERIAL_DOWNLOAD("learning_material:download"),
    LEARNING_MATERIAL_DELETE("learning_material:delete"),

    
    ;

    @Getter
    private final String permission;
}
