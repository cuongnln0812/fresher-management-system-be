package com.example.phase1_fams.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public enum LearningMaterialPermissionGroup {
    NO_ACCESS(Collections.emptySet()),

    VIEW(EnumSet.of(Permission.LEARNING_MATERIAL_READ, Permission.LEARNING_MATERIAL_DOWNLOAD)),

    MODIFY(EnumSet.of(Permission.LEARNING_MATERIAL_READ, Permission.LEARNING_MATERIAL_DOWNLOAD, Permission.LEARNING_MATERIAL_UPLOAD)),

    FULL_ACCESS(EnumSet.of(Permission.LEARNING_MATERIAL_READ, Permission.LEARNING_MATERIAL_DOWNLOAD, Permission.LEARNING_MATERIAL_UPLOAD, Permission.LEARNING_MATERIAL_DELETE));

    @Getter
    private final Set<Permission> permissions;

}
