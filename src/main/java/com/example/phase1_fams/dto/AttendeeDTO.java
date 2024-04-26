package com.example.phase1_fams.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendeeDTO {
    private String type;
    private int planned;
    private int accepted;
    private int actual;
}
