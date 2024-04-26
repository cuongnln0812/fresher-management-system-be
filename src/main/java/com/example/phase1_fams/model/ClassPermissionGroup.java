package com.example.phase1_fams.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public enum ClassPermissionGroup {
    NO_ACCESS(Collections.emptySet()),
    VIEW(EnumSet.of(Permission.CLASS_READ)),
    MODIFY(EnumSet.of(Permission.CLASS_READ, Permission.CLASS_UPDATE)),
    CREATE(EnumSet.of(Permission.CLASS_READ, Permission.CLASS_UPDATE, Permission.CLASS_CREATE)),

    FULL_ACCESS(EnumSet.of(Permission.CLASS_READ, Permission.CLASS_UPDATE, Permission.CLASS_CREATE, Permission.CLASS_DELETE));

    @Getter
    private final Set<Permission> permissions;

}
