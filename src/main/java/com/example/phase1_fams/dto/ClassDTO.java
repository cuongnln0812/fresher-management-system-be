package com.example.phase1_fams.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClassDTO {
    private Long id;
    private String code;
    private String name;
    private int duration;
    private String location;
    private String fsu;
    private String status;
    private String createdBy;
    private LocalDate createdDate;
}
