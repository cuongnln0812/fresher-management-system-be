package com.example.phase1_fams.controller;

import com.example.phase1_fams.dto.UsersDTO;
import com.example.phase1_fams.dto.request.UsersReqCreate;
import com.example.phase1_fams.dto.request.UsersReqUpdate;
import com.example.phase1_fams.dto.response.UserAvailable;
import com.example.phase1_fams.dto.response.UsersRes;
import com.example.phase1_fams.security.JwtService;
import com.example.phase1_fams.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testAddNewUser_Success() throws Exception {
        UsersReqCreate requestPayload = new UsersReqCreate();
        // populate requestPayload as necessary
//        requestPayload.setName();

        UsersDTO expectedResponse = new UsersDTO();
        // populate expectedResponse as necessary

        given(usersService.addNewUser(any(UsersReqCreate.class))).willReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddNewUser_BlankAttribute_ReturnBadRequest() throws Exception {
        UsersReqCreate requestPayload = new UsersReqCreate();
        // populate requestPayload as necessary

        UsersDTO expectedResponse = new UsersDTO();
        // populate expectedResponse as necessary

        given(usersService.addNewUser(any(UsersReqCreate.class))).willReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUserById_ReturnUsersRes() throws Exception {
        Long userId = 1L;
        // populate requestPayload as necessary

        UsersRes expectedResponse = new UsersRes();
        expectedResponse.setName("user");
        expectedResponse.setId(userId);
        // populate expectedResponse as necessary

        given(usersService.getUserById(userId)).willReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.data.name").value(expectedResponse.getName()))
                .andExpect(jsonPath("$.message").value("Get user's details successfully!"));
    }

    @Test
    public void testChangeUserStatus_ReturnUserRes() throws Exception {
        given(usersService.changeUserStatus(anyLong())).willReturn(new UsersRes());

        mockMvc.perform(put("/api/v1/users/status/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("User status changed successfully"));
    }

    @Test
    public void testUpdateUserRole_ReturnUserRes() throws Exception {
        given(usersService.updateUserRoleById(anyLong(), anyInt())).willReturn(new UsersRes());

        mockMvc.perform(put("/api/v1/users/{userId}", 1)
                        .param("newRoleId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("User role updated successfully"));
    }

    @Test
    public void testUpdateUserInformation_BlankAttribute_ReturnBadRequest() throws Exception {
        UsersReqUpdate requestDto = new UsersReqUpdate(); // Populate as necessary
        given(usersService.updateUserInformation(anyLong(), any(UsersReqUpdate.class)))
                .willReturn(new UsersDTO());

        mockMvc.perform(put("/api/v1/users/information/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getClassAdminList_Success() throws Exception {
        List<UserAvailable> admins = List.of(new UserAvailable(1L, "Jane Doe", "Admin"));
        given(usersService.getClassAdminList(null)).willReturn(admins);

        mockMvc.perform(get("/api/v1/users//class-admins")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void getTrainerList_Success() throws Exception {
        List<UserAvailable> trainers = List.of(new UserAvailable(1L, "John Doe", "Trainer"));
        given(usersService.getTrainerList(null)).willReturn(trainers);

        mockMvc.perform(get("/api/v1/users//trainers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
