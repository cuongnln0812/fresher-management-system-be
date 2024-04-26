package com.example.phase1_fams.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "training_content")
public class TrainingContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;
    @Column(name = "order_number")
    private int orderNumber;
    @Column(name = "content_name")
    private String name;
    private int duration;
    @Column(name = "delivery_type")
    private String deliveryType;
    @Column(name = "training_format")
    private String method;

    @ManyToMany
    @JoinTable(name = "content_objective", joinColumns = @JoinColumn(name = "content_id"), inverseJoinColumns = @JoinColumn(name = "objective_code"))
    private Set<LearningObjective> objectiveCodes = new HashSet<>();

    @OneToMany(mappedBy = "trainingContent", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<LearningMaterial> learningMaterials;
    private Set<LearningMaterial> learningMaterials = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "unit_code")
    private TrainingUnit trainingUnit;
}
