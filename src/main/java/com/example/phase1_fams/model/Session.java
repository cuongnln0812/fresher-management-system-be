package com.example.phase1_fams.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;
    @Column(name = "day_progress")
    private int dayProgress;
    @Column(name = "total_days")
    private int totalDays;
    @Column(name = "class_time")
    private String classTime;
    private String location;
    @Column(name = "trainer_name")
    private String trainerName;
    @Column(name = "admin_name")
    private String adminName;
    @Column(name = "session_date")
    private LocalDate sessionDate;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class aClass;
}
