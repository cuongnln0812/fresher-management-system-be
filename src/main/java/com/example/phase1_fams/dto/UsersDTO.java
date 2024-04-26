package com.example.phase1_fams.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UsersDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private LocalDate dob;

    private String gender;

    private boolean status;

    private String createdBy;

    private LocalDate createdDate;

    private String modifiedBy;

    private LocalDate modifiedDate;

    private boolean isFirstLogin;

    private String roleName;

}
