package com.example.phase1_fams.dto;

import lombok.*;

import java.time.Instant;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private Instant timestamp;
    private String status;
    private int httpStatus;
    private String message;
    private T data;
}