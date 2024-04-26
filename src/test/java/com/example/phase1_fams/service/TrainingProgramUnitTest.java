package com.example.phase1_fams.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.example.phase1_fams.model.DaysUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.model.Syllabus;
import com.example.phase1_fams.model.TrainingProgram;
import com.example.phase1_fams.model.TrainingProgramSyllabus;
import com.example.phase1_fams.repository.SyllabusRepository;
import com.example.phase1_fams.repository.TrainingProgramRepository;
import com.example.phase1_fams.service.impl.TrainingProgramServiceImpl;

public class TrainingProgramUnitTest {

    @Mock
    private SyllabusRepository syllabusRepository;

    @Mock
    private TrainingProgramRepository trainingProgramRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private URLConnection urlConnection;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private TrainingProgramServiceImpl trainingProgramService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDownloadFileFromGoogleDrive() throws Exception {
        // Arrange
        String googleDriveLink = "https://drive.google.com/uc?export=download&id=1euefC_Tdg3vmG0wbgi1IAAV9xLpdXP2U";
        try {
            // Mock URL and URLConnection
            URL url = mock(URL.class);
            when(url.openConnection()).thenReturn(urlConnection);

            // Mock InputStream
            when(urlConnection.getInputStream()).thenReturn(inputStream);

            // Call the download function
            InputStreamResource result = trainingProgramService.downloadFileFromGoogleDrive(googleDriveLink);

            // Assertions/asserting the result
            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @Test
    void testImportTrainingProgramWithUniqueTrainingProgram() {
        TrainingProgram program1 = new TrainingProgram();
        program1.setName("Training Program 1");
        program1.setDescription("Description 1");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        Syllabus newSyl = createTestSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);

        TrainingProgram program2 = new TrainingProgram();
        program2.setName("Training Program 2");
        program2.setDescription("Description 2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();
        syl2.setSyllabus(newSyl2);
        syllabusSet2.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);

        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));
        when(trainingProgramRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Mock authenticationService.getName() to return a dummy user
        when(authenticationService.getName()).thenReturn("dummyUser");
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Create mock Excel data
        byte[] mockExcelData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockExcelData);
        trainingProgramService.importTrainingProgram(mockFile, 1);
        verify(trainingProgramRepository, times(2)).save(any(TrainingProgram.class));
        ArgumentCaptor<TrainingProgram> captor = ArgumentCaptor.forClass(TrainingProgram.class);
        verify(trainingProgramRepository, times(2)).save(captor.capture());
        List<TrainingProgram> savedTrainingPrograms = captor.getAllValues();
        assertEquals(2, savedTrainingPrograms.size());

        program1 = savedTrainingPrograms.get(0);
        assertEquals("Training Program 1", program1.getName());
        assertEquals("Description 1", program1.getDescription());

        program2 = savedTrainingPrograms.get(1);
        assertEquals("Training Program 2", program2.getName());
        assertEquals("Description 2", program2.getDescription());

    }

    @SuppressWarnings("null")
    @Test
    void testImportTrainingProgram_DuplicateOption1() {
        TrainingProgram program1 = new TrainingProgram();
        program1.setName("Training Program 1");
        program1.setDescription("Description 2");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        Syllabus newSyl = createTestSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);
        TrainingProgram program2 = new TrainingProgram();
        program2.setName("Training Program 2");
        program2.setDescription("Description 2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();
        syl2.setSyllabus(newSyl2);
        syllabusSet2.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);

        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));
        when(trainingProgramRepository.findByName("Training Program 1")).thenReturn(Optional.of(program1));
        when(trainingProgramRepository.findByName("Training Program 2")).thenReturn(Optional.of(program2));

        // Set up mock behavior for save
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgram savedProgram = invocation.getArgument(0);// Append _1 to the name
                    when(trainingProgramRepository.findByName(savedProgram.getName()))
                            .thenReturn(Optional.of(savedProgram));
                    return savedProgram;
                });
        // Mock authenticationService.getName() to return a dummy user
        when(authenticationService.getName()).thenReturn("dummyUser");
        // Create mock Excel data
        byte[] mockExcelData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockExcelData);
        trainingProgramService.importTrainingProgram(mockFile, 1);
        verify(trainingProgramRepository, times(2)).save(any(TrainingProgram.class));
        ArgumentCaptor<TrainingProgram> captor = ArgumentCaptor.forClass(TrainingProgram.class);
        verify(trainingProgramRepository, times(2)).save(captor.capture());
        List<TrainingProgram> savedTrainingPrograms = captor.getAllValues();
        assertEquals(2, savedTrainingPrograms.size());

        program1 = savedTrainingPrograms.get(0);
        assertEquals("Training Program 1_1", program1.getName());
        assertEquals("Description 1", program1.getDescription());

        program2 = savedTrainingPrograms.get(1);
        assertEquals("Training Program 2_1", program2.getName());
        assertEquals("Description 2", program2.getDescription());

    }

    @SuppressWarnings("null")
    @Test
    void testImportTrainingProgram_DuplicateOption2() {
        // Mock data
        TrainingProgram program1 = new TrainingProgram();
        program1.setName("Training Program 1");
        program1.setDescription("Description 2");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        Syllabus newSyl = createTestSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);
        TrainingProgram program2 = new TrainingProgram();
        program2.setName("Training Program 2");
        program2.setDescription("Description 2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();
        syl2.setSyllabus(newSyl2);
        syllabusSet2.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);
        byte[] mockExcelData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockExcelData);

        // Mock behavior of dependencies
        when(authenticationService.getName()).thenReturn("dummyUser");
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));
        when(trainingProgramRepository.findByName("Training Program 1")).thenReturn(Optional.of(program1));
        when(trainingProgramRepository.findByName("Training Program 2")).thenReturn(Optional.of(program2));
        when(trainingProgramRepository.save(any(TrainingProgram.class))).thenReturn(program1);
        doNothing().when(trainingProgramRepository).delete(any(TrainingProgram.class));
        // Call the method
        trainingProgramService.importTrainingProgram(mockFile, 2);
        ArgumentCaptor<TrainingProgram> captor = ArgumentCaptor.forClass(TrainingProgram.class);
        // Verify interactions
        verify(trainingProgramRepository, times(2)).findByName("Training Program 1");
        verify(trainingProgramRepository, times(2)).findByName("Training Program 2");
        verify(trainingProgramRepository, times(1)).delete(program1);
        verify(trainingProgramRepository, times(1)).delete(program2);
        verify(trainingProgramRepository, times(2)).save(captor.capture());
        List<TrainingProgram> savedTrainingPrograms = captor.getAllValues();

        // Assertions
        assertEquals(2, savedTrainingPrograms.size());
        TrainingProgram savedProgram = savedTrainingPrograms.get(0);
        assertEquals("Training Program 1", savedProgram.getName()); // Name remains the same after replacement
        assertEquals("Description 1", savedProgram.getDescription());
        TrainingProgram savedProgram2 = savedTrainingPrograms.get(1);
        assertEquals("Training Program 2", savedProgram2.getName()); // Name remains the same after replacement
        assertEquals("Description 2", savedProgram2.getDescription());
    }

    @SuppressWarnings("null")
    @Test
    void testImportTrainingProgram_DuplicateOption0() {
        TrainingProgram program1 = new TrainingProgram();
        program1.setName("Training Program 1");
        program1.setDescription("Description 2");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        Syllabus newSyl = createTestSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);
        TrainingProgram program2 = new TrainingProgram();
        program2.setName("Training Program 2");
        program2.setDescription("Description 2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();
        syl2.setSyllabus(newSyl2);
        syllabusSet2.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);
        byte[] mockExcelData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockExcelData);
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));
        when(trainingProgramRepository.findByName("Training Program 1")).thenReturn(Optional.of(program1));
        when(trainingProgramRepository.findByName("Training Program 2")).thenReturn(Optional.of(program2));

        // Call the method
        trainingProgramService.importTrainingProgram(mockFile, 0);

        verify(trainingProgramRepository, never()).save(any(TrainingProgram.class));
    }

    @SuppressWarnings("null")
    @Test
    public void testUniqueImportTrainingProgramCSV() throws IOException {
        // Mock file content
        String csvContent = "Name,Information,List Syllabus\n" +
                "Program1,Info1,SYL001\n" +
                "Program2,Info2,SYL002\n";
        Syllabus newSyl = createTestSyllabus();
        TrainingProgram program1 = new TrainingProgram();
        program1.setName("Program1");
        program1.setDescription("Info1");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);

        TrainingProgram program2 = new TrainingProgram();
        program2.setName("Program2");
        program2.setDescription("Info2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();
        syl1.setSyllabus(newSyl2);
        syllabusSet.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);

        // Mock MultipartFile
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Mock syllabusRepository to return a syllabus
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));

        // Mock trainingProgramRepository to return an empty optional for name checks
        when(trainingProgramRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Mock authenticationService.getName() to return a dummy user
        when(authenticationService.getName()).thenReturn("dummyUser");
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // Invoke the method under test
        trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 1);

        // Verify that the trainingProgramRepository.save() is called twice
        verify(trainingProgramRepository, times(2)).save(any(TrainingProgram.class));
        ArgumentCaptor<TrainingProgram> captor = ArgumentCaptor.forClass(TrainingProgram.class);
        verify(trainingProgramRepository, times(2)).save(captor.capture());
        List<TrainingProgram> savedTrainingPrograms = captor.getAllValues();
        assertEquals(2, savedTrainingPrograms.size());

        program1 = savedTrainingPrograms.get(0);
        assertEquals("Program1", program1.getName());
        assertEquals("Info1", program1.getDescription());

        program2 = savedTrainingPrograms.get(1);
        assertEquals("Program2", program2.getName());
        assertEquals("Info2", program2.getDescription());
    }

    @SuppressWarnings("null")
    @Test
    void testImportTrainingProgramCSV_DuplicateOption1() {
        String csvContent = "Name,Information,List Syllabus\n" +
                "Program1,Info1,SYL001\n" +
                "Program2,Info2,SYL002\n";

        // Create mock syllabuses
        Syllabus newSyl = createTestSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();

        // Mock training programs
        TrainingProgram program1 = new TrainingProgram();
        program1.setName("Program1");
        program1.setDescription("Info1");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);

        TrainingProgram program2 = new TrainingProgram();
        program2.setName("Program2");
        program2.setDescription("Info2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        syl2.setSyllabus(newSyl2);
        syllabusSet2.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);

        // Mock MultipartFile
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Mock syllabusRepository to return syllabuses
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));

        // Mock trainingProgramRepository to return an existing program for name checks
        when(trainingProgramRepository.findByName("Program1")).thenReturn(Optional.of(program1));
        when(trainingProgramRepository.findByName("Program2")).thenReturn(Optional.of(program2));

        // Set up mock behavior for save
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgram savedProgram = invocation.getArgument(0);
                    when(trainingProgramRepository.findByName(savedProgram.getName()))
                            .thenReturn(Optional.of(savedProgram));
                    return savedProgram;
                });
        // Mock authenticationService.getName() to return a dummy user
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Invoke the method under test
        trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 1);

        // Verify that the trainingProgramRepository.save() is called twice
        verify(trainingProgramRepository, times(2)).save(any(TrainingProgram.class));
        ArgumentCaptor<TrainingProgram> captor = ArgumentCaptor.forClass(TrainingProgram.class);
        verify(trainingProgramRepository, times(2)).save(captor.capture());
        List<TrainingProgram> savedTrainingPrograms = captor.getAllValues();
        assertEquals(2, savedTrainingPrograms.size());

        // Assert program details
        program1 = savedTrainingPrograms.get(0);
        assertEquals("Program1_1", program1.getName()); // Name should be appended with _1
        assertEquals("Info1", program1.getDescription());

        program2 = savedTrainingPrograms.get(1);
        assertEquals("Program2_1", program2.getName()); // Name should be appended with _1
        assertEquals("Info2", program2.getDescription());
    }

    @SuppressWarnings("null")
    @Test
    public void testImportTrainingProgramCSV_DuplicateOption2() throws IOException {
        // Mock file content
        String csvContent = "Name,Information,List Syllabus\n" +
                "Program1,Info1,SYL001\n" +
                "Program2,Info2,SYL002\n";

        // Create mock syllabuses
        Syllabus newSyl = createTestSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();

        // Mock training programs
        TrainingProgram program1 = new TrainingProgram();
        program1.setId(1L); // Set ID for program 1
        program1.setName("Program1");
        program1.setDescription("Info1");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);

        TrainingProgram program2 = new TrainingProgram();
        program2.setId(2L); // Set ID for program 2
        program2.setName("Program2");
        program2.setDescription("Info2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        syl2.setSyllabus(newSyl2);
        syllabusSet2.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);

        // Mock MultipartFile
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Mock syllabusRepository to return syllabuses
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));

        // Mock trainingProgramRepository to return existing programs for name checks
        when(trainingProgramRepository.findByName("Program1")).thenReturn(Optional.of(program1));
        when(trainingProgramRepository.findByName("Program2")).thenReturn(Optional.of(program2));

        // Mock delete method of trainingProgramRepository
        doNothing().when(trainingProgramRepository).delete(any(TrainingProgram.class));

        // Set up mock behavior for save
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock authenticationService.getName() to return a dummy user
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Invoke the method under test
        trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 2);

        // Verify that the trainingProgramRepository.delete() is called twice
        verify(trainingProgramRepository, times(2)).findByName("Program1");
        verify(trainingProgramRepository, times(2)).findByName("Program2");
        verify(trainingProgramRepository, times(1)).delete(program1);
        verify(trainingProgramRepository, times(1)).delete(program2);
        // Verify that the trainingProgramRepository.save() is called twice
        verify(trainingProgramRepository, times(2)).save(any(TrainingProgram.class));
        ArgumentCaptor<TrainingProgram> captor = ArgumentCaptor.forClass(TrainingProgram.class);
        verify(trainingProgramRepository, times(2)).save(captor.capture());
        List<TrainingProgram> savedTrainingPrograms = captor.getAllValues();
        assertEquals(2, savedTrainingPrograms.size());

        // Assert program details
        program1 = savedTrainingPrograms.get(0);
        assertEquals("Program1", program1.getName()); // Name should remain unchanged
        assertEquals("Info1", program1.getDescription());

        program2 = savedTrainingPrograms.get(1);
        assertEquals("Program2", program2.getName()); // Name should remain unchanged
        assertEquals("Info2", program2.getDescription());
    }

    @SuppressWarnings("null")
    @Test
    void testImportTrainingProgramCSV_DuplicateOption0() {
        // Mock file content
        String csvContent = "Name,Information,List Syllabus\n" +
                "Program1,Info1,SYL001\n" +
                "Program2,Info2,SYL002\n";

        // Create mock syllabuses
        Syllabus newSyl = createTestSyllabus();
        Syllabus newSyl2 = createTestSyllabus2();

        // Mock training programs
        TrainingProgram program1 = new TrainingProgram();
        program1.setName("Program1");
        program1.setDescription("Info1");
        Set<TrainingProgramSyllabus> syllabusSet = new HashSet<>();
        TrainingProgramSyllabus syl1 = new TrainingProgramSyllabus();
        syl1.setSyllabus(newSyl);
        syllabusSet.add(syl1);
        program1.setTrainingProgramSyllabusSet(syllabusSet);

        TrainingProgram program2 = new TrainingProgram();
        program2.setName("Program2");
        program2.setDescription("Info2");
        Set<TrainingProgramSyllabus> syllabusSet2 = new HashSet<>();
        TrainingProgramSyllabus syl2 = new TrainingProgramSyllabus();
        syl2.setSyllabus(newSyl2);
        syllabusSet2.add(syl2);
        program2.setTrainingProgramSyllabusSet(syllabusSet2);

        // Mock MultipartFile
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Mock syllabusRepository to return syllabuses
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(newSyl));
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(newSyl2));

        // Mock trainingProgramRepository to return existing programs for name checks
        when(trainingProgramRepository.findByName("Program1")).thenReturn(Optional.of(program1));
        when(trainingProgramRepository.findByName("Program2")).thenReturn(Optional.of(program2));

        // Set up mock behavior for save
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock authenticationService.getName() to return a dummy user
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Invoke the method under test
        trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 0);

        // Verify that the trainingProgramRepository.save() is called twice
        verify(trainingProgramRepository, times(0)).save(any(TrainingProgram.class));
    }

    @Test
    void testImportTrainingProgramCSV_SyllabusNotFound() throws IOException {
        // Mock file content
        String csvContent = "Name,Information,List Syllabus\n" +
                "Program1,Info1,SYL001\n" +
                "Program2,Info2,SYL002\n";

        // Mock MultipartFile
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Mock syllabusRepository to return empty for syllabus code SYL001
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.empty());
        // Mock syllabusRepository to return a syllabus for syllabus code SYL002
        when(syllabusRepository.findByCode("SYL002")).thenReturn(Optional.of(createTestSyllabus()));

        // Mock authenticationService.getName() to return a dummy user
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Invoke the method under test and expect an ApiException
        assertThrows(ApiException.class, () -> trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 1));
    }

    @Test
    void testImportTrainingProgramCSV_EmptyHeader() throws IOException {
        // Mock CSV content with an empty header
        String csvContent = "Name,,List Syllabus\n" +
                "Program1,Info1,SYL001\n";

        // Mock MultipartFile
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Invoke the method under test and expect an ApiException
        assertThrows(ApiException.class, () -> trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 1));
    }

    @Test
    void testImportTrainingProgramCSV_InformationBeforeName() throws IOException {
        // Mock CSV content with "Information" before "Name"
        String csvContent = "Name,Information,List Syllabus\n" +
                ",Info1,SYL001\n";

        // Mock MultipartFile
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Invoke the method under test and expect an ApiException
        assertThrows(ApiException.class, () -> trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 1),
                "Name must be provided before Information");
    }

    @Test
    void testImportTrainingProgramCSV_NameMustBeforeException() throws IOException {
        // Mock CSV content with "List Syllabus" before "Name"
        String csvContent = "Name,Information,List Syllabus\n" +
                ",,SYL001\n";

        // Mock MultipartFile
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());

        // Invoke the method under test and expect an ApiException
        assertThrows(ApiException.class, () -> trainingProgramService.importTrainingProgramCSV(mockMultipartFile, 1),
                "Name must be provided before List Syllabus");
    }

    @Test
    void testInvalidDuplicateOptionCSV() {
        int invalidOption = 3; // Invalid option
        String csvContent = "Name,Information,List Syllabus\n" +
                "Program1,Info1,SYL001\n" +
                "Program2,Info2,SYL002\n";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv",
                "text/csv", csvContent.getBytes());
        // Invoke the method under test and expect an ApiException
        assertThrows(ApiException.class,
                () -> trainingProgramService.importTrainingProgramCSV(mockMultipartFile, invalidOption),
                "Duplicate option must be 1 or 2 or 0 (allow, replace, skip)");
    }

    private Syllabus createTestSyllabus() {
        Set<DaysUnit> set = new HashSet<>();
        set.add(new DaysUnit());
        set.add(new DaysUnit());
        return Syllabus.builder()
                .code("SYL001")
                .name("Syllabus test")
                .version("1.0.0")
                .level("Beginner")
                .gpaCriteria(60)
                .trainingPrinciple("Test")
                .markingPrinciple("test2")
                .othersPrinciple("test4")
                .waiverCriteriaPrinciple("test3")
                .status(1)
                .reTestPrinciple("test1")
                .technicalRequirements("Test Object")
                .assignmentAssessment(30)
                .quizAssessment(30)
                .finalAssessment(40)
                .finalPracticeAssessment(30)
                .finalTheoryAssessment(70)
                .courseObjectives(null)
                .daysUnits(set)
                .build();
    }

    private Syllabus createTestSyllabus2() {
        Set<DaysUnit> set = new HashSet<>();
        set.add(new DaysUnit());
        set.add(new DaysUnit());
        return Syllabus.builder()
                .code("SYL002")
                .name("Syllabus test")
                .version("1.0.0")
                .level("Beginner")
                .gpaCriteria(60)
                .trainingPrinciple("Test")
                .markingPrinciple("test2")
                .othersPrinciple("test4")
                .waiverCriteriaPrinciple("test3")
                .status(1)
                .reTestPrinciple("test1")
                .technicalRequirements("Test Object")
                .assignmentAssessment(30)
                .quizAssessment(30)
                .finalAssessment(40)
                .finalPracticeAssessment(30)
                .finalTheoryAssessment(70)
                .courseObjectives(null)
                .daysUnits(set)
                .build();
    }

    private byte[] generateMockExcelData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Information");
            headerRow.createCell(2).setCellValue("List Syllabus");

            // Create data rows
            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("Training Program 1");
            dataRow1.createCell(1).setCellValue("Description 1");
            dataRow1.createCell(2).setCellValue("SYL001,SYL002");

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("Training Program 2");
            dataRow2.createCell(1).setCellValue("Description 2");
            dataRow2.createCell(2).setCellValue("SYL001,SYL002");

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
