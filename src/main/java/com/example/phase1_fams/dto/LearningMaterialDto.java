package com.example.phase1_fams.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningMaterialDto {
    private Long fileId;
    private String fileName;
    private String fileType;
    private String downloadURL;
    private String uploadBy;
    private LocalDate uploadDate;
}
