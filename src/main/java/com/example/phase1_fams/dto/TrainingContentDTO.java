package com.example.phase1_fams.dto;

import java.util.Set;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingContentDTO {
    private int orderNumber;
    private String contentName;
    private int duration;
    private Set<String> outputStandards;
    private String deliveryType;
    private String method;
}
