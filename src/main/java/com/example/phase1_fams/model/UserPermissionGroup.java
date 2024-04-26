package com.example.phase1_fams.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public enum UserPermissionGroup {
    NO_ACCESS(Collections.emptySet()),

    VIEW(EnumSet.of(Permission.USER_READ)),

    MODIFY(EnumSet.of(Permission.USER_READ, Permission.USER_UPDATE)),

    CREATE(EnumSet.of(Permission.USER_READ, Permission.USER_UPDATE, Permission.USER_CREATE)),

    FULL_ACCESS(EnumSet.of(Permission.USER_READ, Permission.USER_UPDATE, Permission.USER_CREATE, Permission.USER_DELETE));

    @Getter
    private final Set<Permission> permissions;

}
