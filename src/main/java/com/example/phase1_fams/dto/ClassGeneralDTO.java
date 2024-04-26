package com.example.phase1_fams.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ClassGeneralDTO {
    private LocalTime classStartTime;
    private LocalTime classEndTime;
    private String location;
    private String trainer;
    private Long trainerId;
    private String admin;
    private Long adminId;
    private String fsu;
}
