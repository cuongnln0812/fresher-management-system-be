package com.example.phase1_fams.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersRes {
    private Long id;
    private String name;
    private String email;
    private LocalDate dob;
    private String phone;
    private String gender;
    private String roleName;
    private boolean isFirstLogin;
    private boolean status;

}
