package com.example.phase1_fams.controller;

import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.ClassReq;
import com.example.phase1_fams.dto.request.ClassReqUpdate;
import com.example.phase1_fams.dto.request.SessionReq;
import com.example.phase1_fams.dto.response.ClassDetailsRes;
import com.example.phase1_fams.dto.response.ClassRes;
import com.example.phase1_fams.dto.response.SessionRes;
import com.example.phase1_fams.security.JwtService;
import com.example.phase1_fams.service.ClassService;
import com.example.phase1_fams.service.SessionService;
import com.example.phase1_fams.utils.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ClassControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private ClassService classService;

    @MockBean
    private JwtService jwtService;

    @Test
    void deactivateClass_Success() throws Exception {
        willDoNothing().given(classService).deactivateClass(anyLong());

        mockMvc.perform(put("/api/v1/class/deactivate/{id}", 1L))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Deactivate successfully!"));
    }

    @Test
    void deactivateClass_ApiException() throws Exception {
        long classId = 1L;
        ApiException apiException = new ApiException( HttpStatus.BAD_REQUEST, "Error deactivating class");

        doThrow(apiException).when(classService).deactivateClass(classId);

        mockMvc.perform(put("/api/v1/class/deactivate/{id}", classId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error deactivating class"));
    }


    @Test
    public void testSearchClasses_AllParamsNull_ShouldReturnAll() throws Exception {
        // Given
        when(classService.findFilteredClasses(null, null, null, null, null, null, null,null,
                Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER), Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER)))
                .thenReturn(new PageImpl<>(Collections.singletonList(new ClassDTO()))); // Mock the service call

        // When & Then
        mockMvc.perform(get("/api/v1/class") // Replace /your-endpoint with the actual endpoint
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Get or filter class success!"));
    }

    @Test
    public void testSearchClasses_ApiException_ShouldReturnError() throws Exception {
        // Given
        when(classService.findFilteredClasses(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "Custom error message")); // Simulate service exception

        // When & Then
        mockMvc.perform(get("/api/v1/class") // Adjust as necessary
                        .param("keyword", "test") // Add more parameters as needed to simulate the request
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Custom error message"));
    }

    @Test
    void getClassDetails_Success() throws Exception {
        given(classService.getClassDetails(anyLong()))
                .willReturn(new ClassDetailsRes());

        mockMvc.perform(get("/api/v1/class/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getClassDetails_ApiException() throws Exception {
        long classId = 1L;
        ApiException apiException = new ApiException(HttpStatus.NOT_FOUND, "Class not found");

        doThrow(apiException).when(classService).getClassDetails(classId);

        mockMvc.perform(get("/api/v1/class/{id}", classId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Class not found"));
    }


    @Test
    void getTrainingCalendar_Success() throws Exception {
        given(sessionService.getSessionListByDate(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of(new SessionRes()));

        mockMvc.perform(get("/api/v1/class/calendar")
                        .param("startDate", "2021-01-01")
                        .param("endDate", "2021-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    void createClassAsScheduled_Returns201() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        ClassReq classReq = new ClassReq();// Populate this object as necessary
        classReq.setLocationCode("HCM");
        classReq.setName("DevOp");

        ClassRes classRes = new ClassRes(); // Assume this is the expected response object
        classRes.setCode("HCM_24_01");
        classRes.setName("DevOp Foundation");
        given(classService.createClassAsScheduled(classReq)).willReturn(classRes);

        // When & Then
        mockMvc.perform(post("/api/v1/class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classReq)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Class created successfully!")));
    }

    @Test
    void createClassAsScheduled_BlankAttribute_ReturnBadRequest() throws Exception {
        given(classService.createClassAsScheduled(any(ClassReq.class)))
                .willReturn(new ClassRes());

        mockMvc.perform(post("/api/v1/class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Use a real JSON payload or object mapper to convert ClassReq
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClassAsPlanning_Returns201() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        ClassReq classReq = new ClassReq();// Populate this object as necessary
        classReq.setLocationCode("HCM");
        classReq.setName("DevOp");

        ClassRes classRes = new ClassRes(); // Assume this is the expected response object
        classRes.setCode("HCM_24_01");
        classRes.setName("DevOp Foundation");
        given(classService.createClassAsPlanning(classReq)).willReturn(classRes);

        // When & Then
        mockMvc.perform(post("/api/v1/class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classReq)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Class created successfully!")));
    }

    @Test
    void createClassAsPlanning_BlankAttribute_ReturnBadRequest() throws Exception {
        ClassRes classRes = new ClassRes(); // Mock response object
        // Set properties on classRes as needed

        when(classService.createClassAsPlanning(any(ClassReq.class))).thenReturn(classRes);

        mockMvc.perform(post("/api/v1/class/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Add your JSON content here
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void updateClass_BlankAttribute_ReturnBadRequest() throws Exception {
//        ClassRes classRes = new ClassRes(); // Prepare response
//        // Set properties on classRes
//
//        when(classService.update(anyLong(), any(ClassReqUpdate.class))).thenReturn(classRes);
//
//        mockMvc.perform(put("/api/v1/class/{classId}/", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}")) // Add JSON content
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    void updateSessionInClass_Success() throws Exception {
//        SessionRes mockSessionRes = new SessionRes(); // Prepare your mock response
//        // Set properties on mockSessionRes as needed
//
//        when(classService.updateSessionInClass(anyLong(), any(SessionReq.class))).thenReturn(mockSessionRes);
//
//        mockMvc.perform(put("/api/v1/class/calendar/{sessionId}/", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}")) // Replace "{}" with your actual JSON payload
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("Session updated successfully!")));
//    }
//
//    @Test
//    void updateSessionInClass_EntityNotFound() throws Exception {
//        when(classService.updateSessionInClass(anyLong(), any(SessionReq.class)))
//                .thenThrow(new EntityNotFoundException("Session not found"));
//
//        mockMvc.perform(put("/api/v1/class/calendar/{sessionId}/", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}")) // Replace "{}" with your actual JSON payload
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void updateSessionInClass_InternalServerError() throws Exception {
//        when(classService.updateSessionInClass(anyLong(), any(SessionReq.class)))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        mockMvc.perform(put("/api/v1/class/calendar/{sessionId}/", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}")) // Replace "{}" with your actual JSON payload
//                .andExpect(status().isInternalServerError())
//                .andExpect(content().string(containsString("Error updating session")));
//    }



    @Test
    void updateAllSessionsInClass_Success() throws Exception {
        List<SessionRes> sessionResList = List.of(new SessionRes()); // Mocked response
        // Configure sessionResList as necessary

        when(classService.updateAllSessionsInClass(anyLong(), any(SessionReq.class))).thenReturn(sessionResList);

        mockMvc.perform(put("/api/v1/class/calendar/{classId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // JSON content
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("All sessions in the class updated successfully!")));
    }


}
