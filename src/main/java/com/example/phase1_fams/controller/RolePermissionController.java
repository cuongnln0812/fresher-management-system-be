package com.example.phase1_fams.controller;

import com.example.phase1_fams.dto.RolePermissionDTO;
import com.example.phase1_fams.service.RolePermissionService;
import com.example.phase1_fams.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/permission")
@PreAuthorize("hasAnyRole('Super Admin', 'Master Admin')")
@Tag(name = "REST APIs for Permission Management")
@SecurityRequirement(name = "bearerAuth")
public class RolePermissionController {

    RolePermissionService rolePermissionService;

    @Autowired
    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @Operation(
            summary = "Get all permission for all roles",
            description = "Get permission for each roles: \n" +
                    "FULL_ACCESS: READ/UPDATE/CREATE, \n" +
                    "MODIFY: CREATE/UPDATE, \n" +
                    "READ: READ, \n" +
                    "NO_ACCESS: no permission"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping()
    public ResponseEntity<?> getPermissionForRoles(){
        return ResponseUtils.response(rolePermissionService.getPermissionForRoles(), "Role permission retrieved!", HttpStatus.OK);
    }

    @Operation(
            summary = "Update all permission for all roles",
            description = "Update permission for each roles: \n" +
                    "FULL_ACCESS: READ/UPDATE/CREATE/DELETE/IMPORT, \n" +
                    "CREATE: READ/UPDATE/CREATE, \n" +
                    "MODIFY: CREATE/UPDATE, \n" +
                    "VIEW: READ, \n" +
                    "NO_ACCESS: no permission"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PutMapping()
    public ResponseEntity<?> updatePermissionForRoles(@RequestBody List<RolePermissionDTO> permissionDTOS) {
        List<RolePermissionDTO> list = rolePermissionService.updatePermissionsForRoles(permissionDTOS);
        return ResponseUtils.response(list, "Permission updated successfully", HttpStatus.OK);
    }
}
