package com.example.phase1_fams.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionDTO {

    private String roleName;

    private String syllabusPermission;

    private String trainingProgramPermission;

    private String classPermission;

    private String learningMaterialPermission;

}
