package com.example.phase1_fams.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthRes {
    private String name;
    private String email;
    private LocalDate dob;
    private String phone;
    private String gender;
    private String roleName;
    private String syllabusPermission;
    private String trainingProgramPermission;
    private String classPermission;
    private String learningMaterialPermission;
    private boolean isFirstLogin;
}
