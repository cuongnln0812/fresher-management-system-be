package com.example.phase1_fams.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @Column(name = "role_id")
    private int roleId;
    @Column(name = "role_name")
    private String roleName;

    @Enumerated(EnumType.STRING)
    private SyllabusPermissionGroup syllabusPermissionGroup;

    @Enumerated(EnumType.STRING)
    private TrainingProgramPermissionGroup trainingProgramPermissionGroup;

    @Enumerated(EnumType.STRING)
    private ClassPermissionGroup classPermissionGroup;

    @Enumerated(EnumType.STRING)
    private LearningMaterialPermissionGroup learningMaterialPermissionGroup;

    @Enumerated(EnumType.STRING)
    private UserPermissionGroup userPermissionGroup;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Users> users;



    public List<SimpleGrantedAuthority> getAuthorities(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Syllabus permissions
        if (syllabusPermissionGroup != null) {
            authorities.addAll(
                    syllabusPermissionGroup.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority(perm.getPermission()))
                            .collect(Collectors.toList())
            );
        }

        // Training program permissions
        if (trainingProgramPermissionGroup != null) {
            authorities.addAll(
                    trainingProgramPermissionGroup.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority(perm.getPermission()))
                            .collect(Collectors.toList())
            );
        }

        // Class permissions
        if (classPermissionGroup != null) {
            authorities.addAll(
                    classPermissionGroup.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority(perm.getPermission()))
                            .collect(Collectors.toList())
            );
        }

        // Learning material permissions
        if (learningMaterialPermissionGroup != null) {
            authorities.addAll(
                    learningMaterialPermissionGroup.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority(perm.getPermission()))
                            .collect(Collectors.toList())
            );
        }

        // Users permissions
        if (userPermissionGroup != null) {
            authorities.addAll(
                    userPermissionGroup.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority(perm.getPermission()))
                            .collect(Collectors.toList())
            );
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.roleName));
        return authorities;
    }
}
