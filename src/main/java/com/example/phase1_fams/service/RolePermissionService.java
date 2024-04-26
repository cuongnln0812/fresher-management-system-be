package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.RolePermissionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RolePermissionService {
    List<RolePermissionDTO> getPermissionForRoles();
    List<RolePermissionDTO> updatePermissionsForRoles(List<RolePermissionDTO> permissionsDTO);
}
