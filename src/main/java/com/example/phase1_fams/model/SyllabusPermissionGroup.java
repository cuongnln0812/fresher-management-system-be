package com.example.phase1_fams.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public enum SyllabusPermissionGroup {

    NO_ACCESS(Collections.emptySet()),

    VIEW(EnumSet.of(Permission.SYLLABUS_READ)),

    MODIFY(EnumSet.of(Permission.SYLLABUS_READ, Permission.SYLLABUS_UPDATE)),

    CREATE(EnumSet.of(Permission.SYLLABUS_READ, Permission.SYLLABUS_UPDATE, Permission.SYLLABUS_CREATE, Permission.SYLLABUS_IMPORT)),

    FULL_ACCESS(EnumSet.of(Permission.SYLLABUS_READ, Permission.SYLLABUS_UPDATE, Permission.SYLLABUS_CREATE, Permission.SYLLABUS_DELETE, Permission.SYLLABUS_IMPORT));

    @Getter
    private final Set<Permission> permissions;

}
