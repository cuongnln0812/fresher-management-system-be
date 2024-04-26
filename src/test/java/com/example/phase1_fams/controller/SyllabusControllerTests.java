package com.example.phase1_fams.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.SyllabusReq;
import com.example.phase1_fams.dto.request.SyllabusReqUpdate;
import com.example.phase1_fams.dto.response.ActiveSyllabus;
import com.example.phase1_fams.dto.response.SyllabusDetailsRes;
import com.example.phase1_fams.dto.response.SyllabusPageRes;
import com.example.phase1_fams.security.JwtService;
import com.example.phase1_fams.service.SyllabusService;
import com.example.phase1_fams.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Collections;
import java.util.List;

@WebMvcTest(SyllabusController.class)
@AutoConfigureMockMvc(addFilters = false)
class SyllabusControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SyllabusService syllabusService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testUploadExcelNoSkippedOption() throws Exception {
        doNothing().when(syllabusService).processImportedFile2(any(), anyInt());

        mockMvc.perform(multipart("/api/v1/syllabus/import/{duplicateOption}", 1).file("file", "testdata".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().string("Syllabus imported successfully!"));
    }

    @Test
    public void testUploadExcelSkippedOption() throws Exception {
        doNothing().when(syllabusService).processImportedFile2(any(), anyInt());

        mockMvc.perform(multipart("/api/v1/syllabus/import/{duplicateOption}", 0).file("file", "testdata".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().string("Syllabus imported successfully!(skipped)"));
    }

    @Test
    public void testUploadExcel_ApiException() throws Exception {
        // Mocking the service to throw ApiException
        doThrow(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Test ApiException"))
                .when(syllabusService).processImportedFile2(any(), anyInt());

        mockMvc.perform(multipart("/api/v1/syllabus/import/{duplicateOption}", 0).file("file", "testdata".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error importing syllabus: Test ApiException"));
    }

    @Test
    public void testDownloadSyllabusImportTemplate() throws Exception {
        mockMvc.perform(get("/api/v1/syllabus/template"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"SyllabusTemplate.xlsx\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    public void testCreateSyllabusAsActive_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SyllabusReq syllabusReq = new SyllabusReq();
        syllabusReq.setSyllabusName("Test123");
        syllabusReq.setCode("Test123");

        when(syllabusService.createSyllabusAsActive(syllabusReq)).thenReturn(new SyllabusDetailsRes());

        mockMvc.perform(post("/api/v1/syllabus").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syllabusReq)))
                        .andExpect(status().isCreated());
    }

    @Test
    public void testCreateSyllabusAsActive_BlankAttribute_ReturnBadRequest() throws Exception {
        SyllabusDetailsRes detailsRes = new SyllabusDetailsRes(); // assuming you have necessary setup for SyllabusDetailsRes
        when(syllabusService.createSyllabusAsActive(any(SyllabusReq.class))).thenReturn(detailsRes);

        mockMvc.perform(post("/api/v1/syllabus").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateSyllabusAsDraft_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SyllabusReq syllabusReq = new SyllabusReq();
        syllabusReq.setSyllabusName("Test123");
        syllabusReq.setCode("Test123");

        when(syllabusService.createSyllabusAsDraft(syllabusReq)).thenReturn(new SyllabusDetailsRes());

        mockMvc.perform(post("/api/v1/syllabus").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syllabusReq)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateSyllabusAsDraft_BlankAttribute_ReturnBadRequest() throws Exception {
        SyllabusDetailsRes detailsRes = new SyllabusDetailsRes(); // assuming you have necessary setup for SyllabusDetailsRes
        when(syllabusService.createSyllabusAsDraft(any(SyllabusReq.class))).thenReturn(detailsRes);

        mockMvc.perform(post("/api/v1/syllabus/draft").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testDuplicateSyllabus() throws Exception {
        SyllabusDetailsRes detailsRes = new SyllabusDetailsRes(); // assuming you have necessary setup for SyllabusDetailsRes
        when(syllabusService.duplicateSyllabus(anyString())).thenReturn(detailsRes);

        mockMvc.perform(post("/api/v1/syllabus/duplicate/{code}", "sampleCode"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Duplicated syllabus created successfully"))
                .andExpect(content().json("{}"));
    }

    @Test
    public void getAllUserPageOrSearch_ShouldReturnSyllabusPage() throws Exception {
        // Mocking service call
        Page<SyllabusPageRes> mockPage = new PageImpl<>(Collections.emptyList());
        when(syllabusService.searchSyllabus(any(), any(), any(), any(), anyInt(), any(), any(), anyInt(), anyInt())).thenReturn(mockPage);

        // Mocking ResponseUtils behavior if necessary
        mockMvc.perform(get("/api/v1/syllabus")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Further assertions can be made here regarding the response body
    }

    @Test
    public void testGetSyllabusDetails() throws Exception {
        SyllabusDetailsRes detailsRes = new SyllabusDetailsRes(); // assuming you have necessary setup for SyllabusDetailsRes
        when(syllabusService.getSyllabusDetails(anyString())).thenReturn(detailsRes);

        mockMvc.perform(get("/api/v1/syllabus/{code}", "sampleCode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Get syllabus details successfully"))
                .andExpect(content().json("{}"));
    }

    @Test
    public void testUpdateSyllabusAsActive_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SyllabusReqUpdate syllabusReq = new SyllabusReqUpdate();
        syllabusReq.setSyllabusName("Test123");
        syllabusReq.setCode("Test123");

        when(syllabusService.updateSyllabusAsActive(syllabusReq)).thenReturn(new SyllabusDetailsRes());

        mockMvc.perform(post("/api/v1/syllabus").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syllabusReq)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateSyllabusAsActive_BlankAttribute_ReturnBadRequest() throws Exception {
        SyllabusDetailsRes detailsRes = new SyllabusDetailsRes(); // assuming you have necessary setup for SyllabusDetailsRes
        when(syllabusService.updateSyllabusAsActive(any(SyllabusReqUpdate.class))).thenReturn(detailsRes);

        mockMvc.perform(put("/api/v1/syllabus").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testUpdateSyllabusAsDraft_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SyllabusReqUpdate syllabusReq = new SyllabusReqUpdate();
        syllabusReq.setSyllabusName("Test123");
        syllabusReq.setCode("Test123");

        when(syllabusService.updateSyllabusAsDraft(syllabusReq)).thenReturn(new SyllabusDetailsRes());

        mockMvc.perform(post("/api/v1/syllabus").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syllabusReq)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateSyllabusAsDraft_BlankAttribute_ReturnBadRequest() throws Exception {
        SyllabusDetailsRes detailsRes = new SyllabusDetailsRes(); // assuming you have necessary setup for SyllabusDetailsRes
        when(syllabusService.updateSyllabusAsDraft(any(SyllabusReqUpdate.class))).thenReturn(detailsRes);

        mockMvc.perform(put("/api/v1/syllabus/draft").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testDeactivateSyllabus() throws Exception {
        SyllabusDetailsRes detailsRes = new SyllabusDetailsRes(); // assuming you have necessary setup for SyllabusDetailsRes
        when(syllabusService.deactiveSyllabus(any())).thenReturn(detailsRes);

        mockMvc.perform(delete("/api/v1/syllabus/{code}", "sampleCode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Delete syllabus successfully (soft delete)"))
                .andExpect(content().json("{}"));
    }

    @Test
    public void getActiveSyllabusList_WithoutName_Success() throws Exception {
        List<ActiveSyllabus> expectedList = List.of(new ActiveSyllabus());
        given(syllabusService.getActiveSyllabusList(null)).willReturn(expectedList);

        mockMvc.perform(get("/api/v1/syllabus/active") // Replace with your actual endpoint
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}