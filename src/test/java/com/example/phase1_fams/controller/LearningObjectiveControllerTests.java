package com.example.phase1_fams.controller;

import com.example.phase1_fams.security.JwtService;
import com.example.phase1_fams.service.LearningObjectiveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LearningObjectiveController.class)
@AutoConfigureMockMvc(addFilters = false)
class LearningObjectiveControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningObjectiveService learningObjectiveService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testGetOutputStandardCodeList() throws Exception {
        // Mock the output standard code list returned by the service
        List<String> outputStandardCodeList = Arrays.asList("Code1", "Code2", "Code3");
        when(learningObjectiveService.getAllOutputCode()).thenReturn(outputStandardCodeList);

        mockMvc.perform(get("/api/v1/output-standard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(outputStandardCodeList.size())) // Ensure the correct number of codes is returned
                .andExpect(jsonPath("$[0]").value("Code1")) // Ensure the first code is correct
                .andExpect(jsonPath("$[1]").value("Code2")) // Ensure the second code is correct
                .andExpect(jsonPath("$[2]").value("Code3")); // Ensure the third code is correct
    }
}