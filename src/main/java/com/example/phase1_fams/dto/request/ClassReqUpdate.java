package com.example.phase1_fams.dto.request;

import com.example.phase1_fams.dto.AttendeeDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassReqUpdate {
    @NotBlank(message = "Location code must not be blank")
    private String locationCode;
    @NotBlank(message = "Class name must not be blank")
    private String name;

    private String location;

    private String fsu;

    private String status;

    private String startTime;

    private String endTime;

    private AttendeeDTO attendeeDTO;

    private List<LocalDate> listOfSessionDate;

    private Long trainingProgramId;

    private Set<ClassUserReq> classUserDTOSet = new HashSet<>();

}
