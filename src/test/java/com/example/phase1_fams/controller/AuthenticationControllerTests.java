package com.example.phase1_fams.controller;

import com.example.phase1_fams.auth.AuthenticationController;
import com.example.phase1_fams.auth.AuthenticationRequest;
import com.example.phase1_fams.auth.AuthenticationResponse;
import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.dto.request.PasswordChangeReq;
import com.example.phase1_fams.dto.response.UserAuthRes;
import com.example.phase1_fams.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testAuthenticate() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();
        when(authenticationService.authenticate(request)).thenReturn(new AuthenticationResponse("sampleToken", "Bearer"));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testChangePassword() throws Exception {
        PasswordChangeReq request = new PasswordChangeReq("oldPass", "newPass");

        doNothing().when(authenticationService).changePassword(request.getOldPassword(), request.getNewPassword());

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    public void testGetUserInfo() throws Exception {
        UserAuthRes user = new UserAuthRes();
        when(authenticationService.getUserInfo()).thenReturn(user);

        mockMvc.perform(get("/api/v1/auth/user-information"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("User information retrieved successfully!"));
    }
}
