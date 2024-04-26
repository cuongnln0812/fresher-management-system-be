package com.example.phase1_fams.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public enum TrainingProgramPermissionGroup {
    NO_ACCESS(Collections.emptySet()),

    VIEW(EnumSet.of(Permission.TRAINING_PROGRAM_READ)),

    MODIFY(EnumSet.of(Permission.TRAINING_PROGRAM_READ, Permission.TRAINING_PROGRAM_UPDATE)),

    CREATE(EnumSet.of(Permission.TRAINING_PROGRAM_READ, Permission.TRAINING_PROGRAM_UPDATE, Permission.TRAINING_PROGRAM_CREATE, Permission.TRAINING_PROGRAM_IMPORT)),

    FULL_ACCESS(EnumSet.of(Permission.TRAINING_PROGRAM_READ, Permission.TRAINING_PROGRAM_UPDATE, Permission.TRAINING_PROGRAM_CREATE, Permission.TRAINING_PROGRAM_DELETE, Permission.TRAINING_PROGRAM_IMPORT));

    @Getter
    private final Set<Permission> permissions;

}
