package com.example.phase1_fams.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionReq {
    @NotBlank(message = "Start time must not be blank")
    private String start;
    @NotBlank(message = "End time must not be blank")
    private String end;
    @NotNull(message = "Trainer must not be blank")
    private Long trainerId;
    @NotNull(message = "Admin must not be blank")
    private Long adminId;
    @NotBlank(message = "FSU must not be blank")
    private String fsu;
}
