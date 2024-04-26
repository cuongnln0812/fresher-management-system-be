package com.example.phase1_fams.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.phase1_fams.dto.exception.ApiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phase1_fams.dto.RolePermissionDTO;
import com.example.phase1_fams.model.ClassPermissionGroup;
import com.example.phase1_fams.model.LearningMaterialPermissionGroup;
import com.example.phase1_fams.model.Role;
import com.example.phase1_fams.model.SyllabusPermissionGroup;
import com.example.phase1_fams.model.TrainingProgramPermissionGroup;
import com.example.phase1_fams.repository.RoleRepository;
import com.example.phase1_fams.service.RolePermissionService;

@Service
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RolePermissionServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<RolePermissionDTO> getPermissionForRoles() {
        List<Role> roleList = roleRepository.findAll();
        return roleList.stream().map(role -> modelMapper.map(role, RolePermissionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RolePermissionDTO> updatePermissionsForRoles(List<RolePermissionDTO> permissionsDTO) {
        List<RolePermissionDTO> rolePermissionDTOS = new ArrayList<>();
        for (RolePermissionDTO dto : permissionsDTO) {
            Role role = roleRepository.findByRoleName(dto.getRoleName())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Role not found"));
            // Cập nhật quyền cho từng module
            try {
                role.setSyllabusPermissionGroup(SyllabusPermissionGroup.valueOf(dto.getSyllabusPermission()));
                role.setTrainingProgramPermissionGroup(
                        TrainingProgramPermissionGroup.valueOf(dto.getTrainingProgramPermission()));
                role.setClassPermissionGroup(ClassPermissionGroup.valueOf(dto.getClassPermission()));
                role.setLearningMaterialPermissionGroup(
                        LearningMaterialPermissionGroup.valueOf(dto.getLearningMaterialPermission()));
                Role savedRole = roleRepository.save(role);
                RolePermissionDTO responseDTO = modelMapper.map(savedRole, RolePermissionDTO.class);
                rolePermissionDTOS.add(responseDTO);
            } catch (IllegalArgumentException e) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Wrong permission name or type");
            }
        }
        return rolePermissionDTOS;
    }

}
