package com.example.phase1_fams.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "learning_material")
public class LearningMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "upload_by")
    private String uploadBy;
    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private TrainingContent trainingContent;

    public void setUploadDate() {
        this.uploadDate = LocalDate.now();
    }
}
