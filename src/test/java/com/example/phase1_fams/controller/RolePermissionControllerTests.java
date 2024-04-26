package com.example.phase1_fams.controller;

import com.example.phase1_fams.dto.RolePermissionDTO;
import com.example.phase1_fams.security.JwtService;
import com.example.phase1_fams.service.RolePermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@WebMvcTest(RolePermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class RolePermissionControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolePermissionService rolePermissionService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testGetPermissionForRoles() throws Exception {
        // Mocking the rolePermissionService to return some sample data
        when(rolePermissionService.getPermissionForRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/permission"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Role permission retrieved!"))
                .andExpect(content().json("{}"));
    }

    @Test
    public void testUpdatePermissionForRoles() throws Exception {
        List<RolePermissionDTO> permissionDTOS = new ArrayList<>(); // Add some sample permissionDTOs here
        // Mocking the rolePermissionService to return some processed data
        when(rolePermissionService.updatePermissionsForRoles(any())).thenReturn(permissionDTOS);

        mockMvc.perform(put("/api/v1/users/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(permissionDTOS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Permission updated successfully"))
                .andExpect(content().json("{}"));
    }

    private byte[] asJsonString(List<RolePermissionDTO> permissionDTOS) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsBytes(permissionDTOS);
    }

}