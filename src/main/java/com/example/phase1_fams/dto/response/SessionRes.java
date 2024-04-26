    package com.example.phase1_fams.dto.response;

    import lombok.*;

    import java.time.LocalDate;
    import java.time.LocalTime;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class SessionRes {
        private Long sessionId;
        private Long classId;
        private String classCode;
        private String className;
        private String classStatus;
        private int dayProgress;
        private int totalDays;
        private String classTime;
        private String location;
        private String trainerName;
        private String adminName;
        private LocalDate sessionDate;
        private String fsu;
        private LocalTime start;
        private LocalTime end;
    }
