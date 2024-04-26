package com.example.phase1_fams.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.phase1_fams.dto.TrainingProgramDTO;
import com.example.phase1_fams.dto.TrainingProgramSyllabusDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.TrainingProgramReq;
import com.example.phase1_fams.dto.request.TrainingProgramReqUpdate;
import com.example.phase1_fams.dto.response.TrainingProgramRes;
import com.example.phase1_fams.model.Syllabus;
import com.example.phase1_fams.security.JwtService;
import com.example.phase1_fams.service.TrainingProgramService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@WebMvcTest(TrainingProgramController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TrainingProgramControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingProgramService trainingProgramService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testViewTrainingProgramDetails() throws Exception {
        when(trainingProgramService.getTrainingProgramDetails(anyLong())).thenReturn(new TrainingProgramRes());

        mockMvc.perform(get("/api/v1/training-program/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Get training program details successfully!"))
                .andExpect(content().json("{}"));
    }

    @Test
    public void testDownloadTrainingProgramImportTemplate() throws Exception {

        mockMvc.perform(get("/api/v1/training-program/template"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"TrainingProgramTemplate.xlsx\""));

    }

    @Test
    public void testCreateTrainingProgramAsActive_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Syllabus syllabus = new Syllabus();
        syllabus.setCode("TEST123");
        syllabus.setName("tester");
        syllabus.setStatus(1);
        TrainingProgramSyllabusDTO trainingProgramSyllabusDTO = new TrainingProgramSyllabusDTO();
        trainingProgramSyllabusDTO.setSyllabusCode(syllabus.getCode());
        trainingProgramSyllabusDTO.setSequence(1);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq();
        trainingProgramReq.setName("Training Program 1");
        trainingProgramReq.setTrainingProgramDTOSet(Set.of(trainingProgramSyllabusDTO));

        when(trainingProgramService.createTrainingProgramAsActive(trainingProgramReq)).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(post("/api/v1/training-program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingProgramReq)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateTrainingProgramAsActive_BlankAttribute_ReturnBadRequest() throws Exception {
        when(trainingProgramService.createTrainingProgramAsActive(any())).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(post("/api/v1/training-program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testCreateTrainingProgramAsDraft_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Syllabus syllabus = new Syllabus();
        syllabus.setCode("TEST123");
        syllabus.setName("tester");
        syllabus.setStatus(1);
        TrainingProgramSyllabusDTO trainingProgramSyllabusDTO = new TrainingProgramSyllabusDTO();
        trainingProgramSyllabusDTO.setSyllabusCode(syllabus.getCode());
        trainingProgramSyllabusDTO.setSequence(1);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq();
        trainingProgramReq.setName("Training Program 1");
        trainingProgramReq.setTrainingProgramDTOSet(Set.of(trainingProgramSyllabusDTO));

        when(trainingProgramService.createTrainingProgramAsDraft(trainingProgramReq)).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(post("/api/v1/training-program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingProgramReq)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateTrainingProgramAsDraft_BlankAttribute_ReturnBadRequest() throws Exception {
        when(trainingProgramService.createTrainingProgramAsDraft(any())).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(post("/api/v1/training-program/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testUpdateTrainingProgramAsActive_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Syllabus syllabus = new Syllabus();
        syllabus.setCode("TEST123");
        syllabus.setName("tester");
        syllabus.setStatus(1);
        TrainingProgramSyllabusDTO trainingProgramSyllabusDTO = new TrainingProgramSyllabusDTO();
        trainingProgramSyllabusDTO.setSyllabusCode(syllabus.getCode());
        trainingProgramSyllabusDTO.setSequence(1);
        TrainingProgramReqUpdate trainingProgramReq = new TrainingProgramReqUpdate();
        trainingProgramReq.setId(1L);
        trainingProgramReq.setName("Training Program 1");
        trainingProgramReq.setTrainingProgramDTOSet(Set.of(trainingProgramSyllabusDTO));

        when(trainingProgramService.updateTrainingProgramAsActive(trainingProgramReq)).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(post("/api/v1/training-program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingProgramReq)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateTrainingProgramAsActive_BlankAttribute_ReturnBadRequest() throws Exception {
        when(trainingProgramService.updateTrainingProgramAsActive(any(TrainingProgramReqUpdate.class))).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(put("/api/v1/training-program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testUpdateTrainingProgramAsDraft_Success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Syllabus syllabus = new Syllabus();
        syllabus.setCode("TEST123");
        syllabus.setName("tester");
        syllabus.setStatus(1);
        TrainingProgramSyllabusDTO trainingProgramSyllabusDTO = new TrainingProgramSyllabusDTO();
        trainingProgramSyllabusDTO.setSyllabusCode(syllabus.getCode());
        trainingProgramSyllabusDTO.setSequence(1);
        TrainingProgramReqUpdate trainingProgramReq = new TrainingProgramReqUpdate();
        trainingProgramReq.setId(1L);
        trainingProgramReq.setName("Training Program 1");
        trainingProgramReq.setTrainingProgramDTOSet(Set.of(trainingProgramSyllabusDTO));

        when(trainingProgramService.updateTrainingProgramAsActive(trainingProgramReq)).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(post("/api/v1/training-program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingProgramReq)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateTrainingProgramAsDraft_BlankAttribute_ReturnBadRequest() throws Exception {
        when(trainingProgramService.updateTrainingProgramAsDraft(any(TrainingProgramReqUpdate.class))).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(put("/api/v1/training-program/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testUploadExcel_ValidExcelFile_NotZeroDuplicateOption() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.xlsx");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, inputStream);

        doNothing().when(trainingProgramService).importTrainingProgram(any(), anyInt());

        mockMvc.perform(multipart("/api/v1/training-program/import/1")
                        .file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(content().string("Training Program imported successfully!"));
    }

    @Test
    public void testUploadExcel_ValidCSVFile_NotZeroDuplicateOption() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, inputStream);

        doNothing().when(trainingProgramService).importTrainingProgram(any(), anyInt());

        mockMvc.perform(multipart("/api/v1/training-program/import/1")
                        .file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(content().string("Training Program imported successfully!"));
    }

    @Test
    public void testUploadExcel_UnsupportedFileFormat() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.txt");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, inputStream);

        mockMvc.perform(multipart("/api/v1/training-program/import/1")
                        .file(multipartFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error importing training program: Unsupported file format"));
    }

    @Test
    public void testUploadExcel_ValidExcelFile_ZeroDuplicateOption() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.xlsx");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, inputStream);

        doNothing().when(trainingProgramService).importTrainingProgram(any(), anyInt());

        mockMvc.perform(multipart("/api/v1/training-program/import/0")
                        .file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(content().string("Training Program imported successfully! (skipped)"));
    }

    @Test
    public void testUploadExcel_ValidCSVFile_ZeroDuplicateOption() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, inputStream);

        doNothing().when(trainingProgramService).importTrainingProgram(any(), anyInt());

        mockMvc.perform(multipart("/api/v1/training-program/import/0")
                        .file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(content().string("Training Program imported successfully! (skipped)"));
    }

    @Test
    public void testDuplicateTrainingProgram() throws Exception {
        when(trainingProgramService.duplicateTrainingProgram(anyLong())).thenReturn(new TrainingProgramRes(/* initialize with necessary values */));

        mockMvc.perform(post("/api/v1/training-program/duplicate/{id}", 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Duplicated Training Program successfully"))
                .andExpect(content().json("{}"));
    }

    @Test
    void searchTrainingPrograms_ShouldReturnTrainingProgramsSuccessfully() throws Exception {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<TrainingProgramDTO> trainingProgramDTOList = Collections.singletonList(new TrainingProgramDTO()); // Replace with actual creation of a DTO
        Page<TrainingProgramDTO> trainingProgramsPage = new PageImpl<>(trainingProgramDTOList, pageRequest, trainingProgramDTOList.size());

        given(trainingProgramService.searchTrainingPrograms(anyString(), anyList(), any(), any(), anyInt(), anyList(), any(PageRequest.class)))
                .willReturn(trainingProgramsPage);

        // When & Then
        mockMvc.perform(get("/api/v1/training-program") // Replace "/your-endpoint" with the actual mapping
                        .param("keyword", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                // Use jsonPath or other assertions to validate the structure and content of the response
    }

    @Test
    public void testSwitchStatus() throws Exception {
        when(trainingProgramService.switchStatus(anyLong())).thenReturn(new TrainingProgramRes());

        mockMvc.perform(put("/api/v1/training-program/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("Change Training Program status successfully"))
                .andExpect(content().json("{}"));
    }

    @Test
    public void getActiveTrainingProgramList_Success() throws Exception {
        // Arrange
        List<TrainingProgramDTO> expectedList = List.of(new TrainingProgramDTO(/* set properties as needed */));
        given(trainingProgramService.getActiveTrainingProgramList(null)).willReturn(expectedList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/training-program/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}

