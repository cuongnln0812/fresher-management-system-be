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
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.model.DaysUnit;
import com.example.phase1_fams.model.LearningObjective;
import com.example.phase1_fams.model.Syllabus;
import com.example.phase1_fams.model.TrainingContent;
import com.example.phase1_fams.model.TrainingUnit;
import com.example.phase1_fams.repository.DaysUnitRepository;
import com.example.phase1_fams.repository.LearningObjectiveRepository;
import com.example.phase1_fams.repository.SyllabusRepository;
import com.example.phase1_fams.repository.TrainingContentRepository;
import com.example.phase1_fams.repository.TrainingProgramSyllabusRepository;
import com.example.phase1_fams.repository.TrainingUnitRepository;
import com.example.phase1_fams.service.impl.SyllabusServiceImpl;

import lombok.NonNull;

public class SyllabusUnitTest {
    @Mock
    private SyllabusRepository syllabusRepository;

    @Mock
    private DaysUnitRepository daysUnitRepository;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private TrainingUnitRepository trainingUnitRepository;
    @Mock
    private TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;

    @Mock
    private TrainingContentRepository trainingContentRepository;

    @Mock
    private LearningObjectiveRepository learningObjectiveRepository;
    @Mock
    private URLConnection urlConnection;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private SyllabusServiceImpl syllabusServiceImpl;

    // Create LearningObjectives
    LearningObjective lojb1 = LearningObjective.builder()
            .code("H4SD")
            .name("TestName")
            .description("h4sd")
            .type("Java")
            .build();

    LearningObjective lojb2 = LearningObjective.builder()
            .code("H5SD")
            .name("TestName2")
            .description("h5sd")
            .type("Java")
            .build();

    // Create TrainingContent
    @NonNull
    TrainingContent content1 = TrainingContent.builder()
            .name("Test Content Name")
            .duration(60)
            .deliveryType("Online")
            .method("Lecture")
            .build();

    // Create Set of TrainingContents
    @NonNull
    TrainingUnit unit1 = TrainingUnit.builder()
            .unitNumber(1)
            .unitName("Test Unit Name")
            .build();

    @NonNull
    DaysUnit day1 = DaysUnit.builder()
            .dayNumber(1) // Set the training units for the day
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testDownloadFileFromGoogleDrive() throws Exception {
        // Arrange
        String googleDriveLink = "https://drive.google.com/uc?export=download&id=19TEkYDxe63vQb0lAX7BR2MQ3JZ0Mr011";
        try {
            // Mock URL and URLConnection
            URL url = mock(URL.class);
            when(url.openConnection()).thenReturn(urlConnection);

            // Mock InputStream
            when(urlConnection.getInputStream()).thenReturn(inputStream);

            // Call the download function
            InputStreamResource result = syllabusServiceImpl.downloadFileFromGoogleDrive(googleDriveLink);

            // Assertions/asserting the result
            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testDownloadFileFromGoogleDrive_IOError() {
        // Arrange
        String googleDriveLink = "";
        try {
            // Mock URL and URLConnection
            URL url = mock(URL.class);
            when(url.openConnection()).thenReturn(urlConnection);

            // Simulate IOException when getting input stream
            when(urlConnection.getInputStream()).thenThrow(new IOException("Failed to open connection"));

            // Act
            InputStreamResource result = syllabusServiceImpl.downloadFileFromGoogleDrive(googleDriveLink);

            // Assert
            assertNull(result); // Expecting null as method should return null on IOException
        } catch (IOException e) {
            // Fail the test if an unexpected IOException occurs
            fail("Unexpected IOException thrown: " + e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testProcessImportedFile2_DuplicateOption1() {
        // Mock existing syllabus returned by findByCode
        Syllabus existingSyllabus = createTestSyllabus();
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(existingSyllabus));

        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> {
            Syllabus savedSyllabus = invocation.getArgument(0);
            savedSyllabus.setCode("SYL001_1"); // Assuming this is how the code gets modified

            // Mock findByCode to return the saved syllabus after save
            when(syllabusRepository.findByCode("SYL001_1")).thenReturn(Optional.of(savedSyllabus));

            return savedSyllabus;
        });
        when(learningObjectiveRepository.findByCode("H4SD")).thenReturn(Optional.of(lojb1));
        when(learningObjectiveRepository.findByCode("H5SD")).thenReturn(Optional.of(lojb2));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenReturn(day1);
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenReturn(unit1);
        when(trainingContentRepository.save(any(TrainingContent.class))).thenReturn(content1);
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Prepare mock file data
        byte[] mockFileData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        // Call the method under test
        syllabusServiceImpl.processImportedFile2(mockFile, 1);

        // Verify that findByCode was called with "SYL001"
        verify(syllabusRepository, times(2)).findByCode("SYL001");

        verify(syllabusRepository, times(1)).save(argThat(syllabus -> syllabus.getCode().equals("SYL001_1")));
        verify(daysUnitRepository, times(3)).save(argThat(day -> day.getDayNumber() == 1));

        // Verify that save was called for unit1
        verify(trainingUnitRepository, times(3))
                .save(argThat(unit -> unit.getUnitNumber() == 1 && unit.getUnitName().equals("Test Unit Name")));

        // Verify that save was called for content1
        verify(trainingContentRepository, times(1))
                .save(argThat(content -> content.getName().equals("Test Content Name")));
    }

    @SuppressWarnings("null")
    @Test
    public void testProcessImportedFile2_DuplicateOption2() {
        // Mock existing syllabus returned by findByCode
        Syllabus existingSyllabus = createTestSyllabus();
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(existingSyllabus));
        doNothing().when(trainingProgramSyllabusRepository).deleteBySyllabusCode("SYL001");
        doNothing().when(syllabusRepository).delete(any(Syllabus.class));
        when(syllabusRepository.save(any(Syllabus.class))).thenReturn(existingSyllabus);
        when(learningObjectiveRepository.findByCode("H4SD")).thenReturn(Optional.of(lojb1));
        when(learningObjectiveRepository.findByCode("H5SD")).thenReturn(Optional.of(lojb2));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenReturn(day1);
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenReturn(unit1);
        when(trainingContentRepository.save(any(TrainingContent.class))).thenReturn(content1);
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Prepare mock file data
        byte[] mockFileData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        // Call the method under test
        syllabusServiceImpl.processImportedFile2(mockFile, 2);

        // Verify that findByCode was called with "SYL001"
        verify(syllabusRepository, times(3)).findByCode("SYL001");

        verify(syllabusRepository, times(1)).save(argThat(syllabus -> syllabus.getCode().equals("SYL001")));
        verify(daysUnitRepository, times(3)).save(argThat(day -> day.getDayNumber() == 1));

        // Verify that save was called for unit1
        verify(trainingUnitRepository, times(3))
                .save(argThat(unit -> unit.getUnitNumber() == 1 && unit.getUnitName().equals("Test Unit Name")));

        // Verify that save was called for content1
        verify(trainingContentRepository, times(1))
                .save(argThat(content -> content.getName().equals("Test Content Name")));
    }

    @SuppressWarnings("null")
    @Test
    public void testProcessImportedFile2_DuplicateOption0() {
        // Mock existing syllabus returned by findByCode
        Syllabus existingSyllabus = createTestSyllabus();
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(existingSyllabus));
        when(syllabusRepository.save(any(Syllabus.class))).thenReturn(null);
        when(learningObjectiveRepository.findByCode("H4SD")).thenReturn(Optional.of(lojb1));
        when(learningObjectiveRepository.findByCode("H5SD")).thenReturn(Optional.of(lojb2));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenReturn(null);
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenReturn(null);
        when(trainingContentRepository.save(any(TrainingContent.class))).thenReturn(null);
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Prepare mock file data
        byte[] mockFileData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        // Call the method under test
        syllabusServiceImpl.processImportedFile2(mockFile, 0);

        // Verify that findByCode was called with "SYL001"
        verify(syllabusRepository, times(2)).findByCode("SYL001");

        verify(syllabusRepository, never()).save(any(Syllabus.class));

        // Verify that save method of daysUnitRepository is never called
        verify(daysUnitRepository, never()).save(any(DaysUnit.class));

        // Verify that save method of trainingUnitRepository is never called
        verify(trainingUnitRepository, never()).save(any(TrainingUnit.class));

        // Verify that save method of trainingContentRepository is never called
        verify(trainingContentRepository, never()).save(any(TrainingContent.class));
    }

    @Test
    public void testInvalidFileType() {
        // Mock MultipartFile with a file having an invalid extension
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "invalid content".getBytes());

        // Call the method under test and expect an IllegalArgumentException
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            syllabusServiceImpl.validateImportedFile(file);
        });
    }

    @Test
    void testInvalidDuplicateOption() {
        Syllabus existingSyllabus = createTestSyllabus();
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(existingSyllabus));
        byte[] mockExcelData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", mockExcelData);

        // Call the method with an invalid duplicate option and assert that it throws
        // the expected ApiException
        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusServiceImpl.processImportedFile2(mockFile, 3));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Duplicate option must be 1 or 2 or 0 (allow, replace, skip)"));
    }

    @SuppressWarnings("null")
    @Test
    public void testProcessImportedFile2_UniqueSyllabus() {
        // Mock existing syllabus returned by findByCode
        Syllabus existingSyllabus = createTestSyllabus();
        when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.empty());

        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> {
            // Return the existing syllabus directly after save
            when(syllabusRepository.findByCode("SYL001")).thenReturn(Optional.of(existingSyllabus));

            return existingSyllabus;
        });
        when(learningObjectiveRepository.findByCode("H4SD")).thenReturn(Optional.of(lojb1));
        when(learningObjectiveRepository.findByCode("H5SD")).thenReturn(Optional.of(lojb2));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenReturn(null);
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenReturn(null);
        when(trainingContentRepository.save(any(TrainingContent.class))).thenReturn(null);
        when(authenticationService.getName()).thenReturn("dummyUser");

        // Prepare mock file data
        byte[] mockFileData = generateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        // Call the method under test
        syllabusServiceImpl.processImportedFile2(mockFile, 1);

        verify(syllabusRepository, times(2)).findByCode("SYL001");

        verify(syllabusRepository, times(1)).save(argThat(syllabus -> syllabus.getCode().equals("SYL001")));
        verify(daysUnitRepository, times(3)).save(argThat(day -> day.getDayNumber() == 1));

        // Verify that save was called for unit1
        verify(trainingUnitRepository, times(3))
                .save(argThat(unit -> unit.getUnitNumber() == 1 && unit.getUnitName().equals("Test Unit Name")));

        // Verify that save was called for content1
        verify(trainingContentRepository, times(1))
                .save(argThat(content -> content.getName().equals("Test Content Name")));
    }

    @SuppressWarnings("null")
    @Test
    public void testProcessImportedFile2_InvalidAssessment1() {

        // Prepare mock file data
        byte[] mockFileData = inValidateMockExcelData1();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusServiceImpl.processImportedFile2(mockFile, 0));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Assessment should larger than 0 and smaller than 100"));

    }

    @SuppressWarnings("null")
    @Test
    public void testProcessImportedFile2_InvalidAssessment2() {

        // Prepare mock file data
        byte[] mockFileData = inValidateMockExcelData2();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusServiceImpl.processImportedFile2(mockFile, 0));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Total Assessment is not equal 100!"));

    }

    @SuppressWarnings("null")
    @Test
    public void testProcessImportedFile2_InvalidAssessment3() {

        // Prepare mock file data
        byte[] mockFileData = inValidateMockExcelData3();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusServiceImpl.processImportedFile2(mockFile, 0));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Final Assessment is not equal 100!"));

    }

    @Test
    public void testProcessImportedFile2_IOException() throws IOException {
        byte[] mockFileData = invalidGenerateMockExcelData();
        MockMultipartFile mockFile = new MockMultipartFile("file", "mock.xlsx", "application/vnd.ms-excel",
                mockFileData);

        // Use assertThrows to verify ApiException handling
        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusServiceImpl.processImportedFile2(mockFile, 1));

        // Verify that ApiException is caught and handled
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("SheetName is wrong!!"));
    }

    @Test
    void duplicateSyllabus_NotFound() {
        // Mock repository behavior
        when(syllabusRepository.findByCode("NON_EXISTENT_CODE")).thenReturn(Optional.empty());

        // Invoke the method
        Exception exception = assertThrows(Exception.class,
                () -> syllabusServiceImpl.duplicateSyllabus("NON_EXISTENT_CODE"));

        // Assertion
        assertTrue(exception.getMessage().contains("Chosen syllabus not found"));
    }

    private Syllabus createTestSyllabus() {
        return Syllabus.builder()
                .code("SYL001")
                .name("Syllabus 1")
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
                .daysUnits(new HashSet<>())
                .build();
    }

    private byte[] generateMockExcelData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            // SyllabusData Sheet
            Sheet syllabusSheet = workbook.createSheet("SyllabusData");

            // Create header row for SyllabusData
            Row syllabusHeaderRow = syllabusSheet.createRow(0);
            syllabusHeaderRow.createCell(0).setCellValue("Code");
            syllabusHeaderRow.createCell(1).setCellValue("Name");
            syllabusHeaderRow.createCell(2).setCellValue("Version");
            syllabusHeaderRow.createCell(3).setCellValue("Course Object");
            syllabusHeaderRow.createCell(4).setCellValue("Level");
            syllabusHeaderRow.createCell(5).setCellValue("Technical Requirements");
            syllabusHeaderRow.createCell(6).setCellValue("Quiz");
            syllabusHeaderRow.createCell(7).setCellValue("Assignment");
            syllabusHeaderRow.createCell(8).setCellValue("Final Practice");
            syllabusHeaderRow.createCell(9).setCellValue("Final Exam");
            syllabusHeaderRow.createCell(10).setCellValue("Final Theory");
            syllabusHeaderRow.createCell(11).setCellValue("GPA criteria");
            syllabusHeaderRow.createCell(12).setCellValue("Training Principle");
            syllabusHeaderRow.createCell(13).setCellValue("Retest Principle");
            syllabusHeaderRow.createCell(14).setCellValue("Marking Principle");
            syllabusHeaderRow.createCell(15).setCellValue("Waiver Criteria Principle");
            syllabusHeaderRow.createCell(16).setCellValue("Others Principle");

            Row syllabusDataRow = syllabusSheet.createRow(1);
            syllabusDataRow.createCell(0).setCellValue("SYL001");
            syllabusDataRow.createCell(1).setCellValue("Syllabus 1");
            syllabusDataRow.createCell(2).setCellValue(1);
            syllabusDataRow.createCell(3).setCellValue("Test Object");
            syllabusDataRow.createCell(4).setCellValue("Beginner");
            syllabusDataRow.createCell(5).setCellValue("TR test");
            syllabusDataRow.createCell(6).setCellValue(30);
            syllabusDataRow.createCell(7).setCellValue(30);
            syllabusDataRow.createCell(8).setCellValue(30);
            syllabusDataRow.createCell(9).setCellValue(40);
            syllabusDataRow.createCell(10).setCellValue(70);
            syllabusDataRow.createCell(11).setCellValue(60);
            syllabusDataRow.createCell(12).setCellValue("Test");
            syllabusDataRow.createCell(13).setCellValue("test1");
            syllabusDataRow.createCell(14).setCellValue("test2");
            syllabusDataRow.createCell(15).setCellValue("test3");
            syllabusDataRow.createCell(16).setCellValue("test4");
            // Add more data as needed for SyllabusData

            // TrainingData Sheet
            Sheet trainingSheet = workbook.createSheet("TrainingData");

            // Create header row for TrainingData
            Row trainingHeaderRow = trainingSheet.createRow(0);
            trainingHeaderRow.createCell(0).setCellValue("Day");
            trainingHeaderRow.createCell(1).setCellValue("Unit Number");
            trainingHeaderRow.createCell(2).setCellValue("Unit Name");
            trainingHeaderRow.createCell(3).setCellValue("Content Number");
            trainingHeaderRow.createCell(4).setCellValue("Content Name");
            trainingHeaderRow.createCell(5).setCellValue("Output Standards");
            trainingHeaderRow.createCell(6).setCellValue("Duration (minutes)");
            trainingHeaderRow.createCell(7).setCellValue("Delivery Type");
            trainingHeaderRow.createCell(8).setCellValue("Method");
            // Add more headers as needed for TrainingData

            // Create data row for TrainingData
            Row trainingDataRow = trainingSheet.createRow(1);
            trainingDataRow.createCell(0).setCellValue(1);
            trainingDataRow.createCell(1).setCellValue(1);
            trainingDataRow.createCell(2).setCellValue("Test Unit Name");
            trainingDataRow.createCell(3).setCellValue(1);
            trainingDataRow.createCell(4).setCellValue("Test Content Name");
            trainingDataRow.createCell(5).setCellValue("H4SD, H5SD");
            trainingDataRow.createCell(6).setCellValue(30);
            trainingDataRow.createCell(7).setCellValue("Concept/Lecture");
            trainingDataRow.createCell(8).setCellValue("Online");
            Row trainingDataRow2 = trainingSheet.createRow(2);
            trainingDataRow2.createCell(0).setCellValue("");
            trainingDataRow2.createCell(1).setCellValue("");
            trainingDataRow2.createCell(2).setCellValue("");
            trainingDataRow2.createCell(3).setCellValue(2);
            trainingDataRow2.createCell(4).setCellValue("Test Content Name 2");
            trainingDataRow2.createCell(5).setCellValue("H4SD, H5SD");
            trainingDataRow2.createCell(6).setCellValue(30);
            trainingDataRow2.createCell(7).setCellValue("Concept/Lecture");
            trainingDataRow2.createCell(8).setCellValue("Online");

            // Add more data as needed for TrainingData

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] invalidGenerateMockExcelData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            // SyllabusData Sheet
            Sheet syllabusSheet = workbook.createSheet("SyllabusData");

            // Create header row for SyllabusData
            Row syllabusHeaderRow = syllabusSheet.createRow(0);
            syllabusHeaderRow.createCell(0).setCellValue("Code");
            syllabusHeaderRow.createCell(1).setCellValue("Name");
            syllabusHeaderRow.createCell(2).setCellValue("Version");
            syllabusHeaderRow.createCell(3).setCellValue("Course Object");
            syllabusHeaderRow.createCell(4).setCellValue("Level");
            syllabusHeaderRow.createCell(5).setCellValue("Technical Requirements");
            syllabusHeaderRow.createCell(6).setCellValue("Quiz");
            syllabusHeaderRow.createCell(7).setCellValue("Assignment");
            syllabusHeaderRow.createCell(8).setCellValue("Final Practice");
            syllabusHeaderRow.createCell(9).setCellValue("Final Exam");
            syllabusHeaderRow.createCell(10).setCellValue("Final Theory");
            syllabusHeaderRow.createCell(11).setCellValue("GPA criteria");
            syllabusHeaderRow.createCell(12).setCellValue("Training Principle");
            syllabusHeaderRow.createCell(13).setCellValue("Retest Principle");
            syllabusHeaderRow.createCell(14).setCellValue("Marking Principle");
            syllabusHeaderRow.createCell(15).setCellValue("Waiver Criteria Principle");
            syllabusHeaderRow.createCell(16).setCellValue("Others Principle");

            Row syllabusDataRow = syllabusSheet.createRow(1);
            syllabusDataRow.createCell(0).setCellValue("SYL001");
            syllabusDataRow.createCell(1).setCellValue("Syllabus 1");
            syllabusDataRow.createCell(2).setCellValue(1);
            syllabusDataRow.createCell(3).setCellValue("Test Object");
            syllabusDataRow.createCell(4).setCellValue("Beginner");
            syllabusDataRow.createCell(5).setCellValue("TR test");
            syllabusDataRow.createCell(6).setCellValue(30);
            syllabusDataRow.createCell(7).setCellValue(30);
            syllabusDataRow.createCell(8).setCellValue(30);
            syllabusDataRow.createCell(9).setCellValue(40);
            syllabusDataRow.createCell(10).setCellValue(70);
            syllabusDataRow.createCell(11).setCellValue(60);
            syllabusDataRow.createCell(12).setCellValue("Test");
            syllabusDataRow.createCell(13).setCellValue("test1");
            syllabusDataRow.createCell(14).setCellValue("test2");
            syllabusDataRow.createCell(15).setCellValue("test3");
            syllabusDataRow.createCell(16).setCellValue("test4");
            // Add more data as needed for SyllabusData

            // TrainingData Sheet
            Sheet trainingSheet = workbook.createSheet("...");

            // Create header row for TrainingData
            Row trainingHeaderRow = trainingSheet.createRow(0);
            trainingHeaderRow.createCell(0).setCellValue("Day");
            trainingHeaderRow.createCell(1).setCellValue("Unit Number");
            trainingHeaderRow.createCell(2).setCellValue("Unit Name");
            trainingHeaderRow.createCell(3).setCellValue("Content Number");
            trainingHeaderRow.createCell(4).setCellValue("Content Name");
            trainingHeaderRow.createCell(5).setCellValue("Output Standards");
            trainingHeaderRow.createCell(6).setCellValue("Duration (minutes)");
            trainingHeaderRow.createCell(7).setCellValue("Delivery Type");
            trainingHeaderRow.createCell(8).setCellValue("Method");
            // Add more headers as needed for TrainingData

            // Create data row for TrainingData
            Row trainingDataRow = trainingSheet.createRow(1);
            trainingDataRow.createCell(0).setCellValue(1);
            trainingDataRow.createCell(1).setCellValue(1);
            trainingDataRow.createCell(2).setCellValue("Test Unit Name");
            trainingDataRow.createCell(3).setCellValue(1);
            trainingDataRow.createCell(4).setCellValue("Test Content Name");
            trainingDataRow.createCell(5).setCellValue("H4SD, H5SD");
            trainingDataRow.createCell(6).setCellValue(30);
            trainingDataRow.createCell(7).setCellValue("Concept/Lecture");
            trainingDataRow.createCell(8).setCellValue("Online");

            // Add more data as needed for TrainingData

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] inValidateMockExcelData1() {
        try (Workbook workbook = new XSSFWorkbook()) {
            // SyllabusData Sheet
            Sheet syllabusSheet = workbook.createSheet("SyllabusData");

            // Create header row for SyllabusData
            Row syllabusHeaderRow = syllabusSheet.createRow(0);
            syllabusHeaderRow.createCell(0).setCellValue("Code");
            syllabusHeaderRow.createCell(1).setCellValue("Name");
            syllabusHeaderRow.createCell(2).setCellValue("Version");
            syllabusHeaderRow.createCell(3).setCellValue("Course Object");
            syllabusHeaderRow.createCell(4).setCellValue("Level");
            syllabusHeaderRow.createCell(5).setCellValue("Technical Requirements");
            syllabusHeaderRow.createCell(6).setCellValue("Quiz");
            syllabusHeaderRow.createCell(7).setCellValue("Assignment");
            syllabusHeaderRow.createCell(8).setCellValue("Final Practice");
            syllabusHeaderRow.createCell(9).setCellValue("Final Exam");
            syllabusHeaderRow.createCell(10).setCellValue("Final Theory");
            syllabusHeaderRow.createCell(11).setCellValue("GPA criteria");
            syllabusHeaderRow.createCell(12).setCellValue("Training Principle");
            syllabusHeaderRow.createCell(13).setCellValue("Retest Principle");
            syllabusHeaderRow.createCell(14).setCellValue("Marking Principle");
            syllabusHeaderRow.createCell(15).setCellValue("Waiver Criteria Principle");
            syllabusHeaderRow.createCell(16).setCellValue("Others Principle");

            Row syllabusDataRow = syllabusSheet.createRow(1);
            syllabusDataRow.createCell(0).setCellValue("SYL001");
            syllabusDataRow.createCell(1).setCellValue("Syllabus 1");
            syllabusDataRow.createCell(2).setCellValue(1);
            syllabusDataRow.createCell(3).setCellValue("Test Object");
            syllabusDataRow.createCell(4).setCellValue("Beginner");
            syllabusDataRow.createCell(5).setCellValue("TR test");
            syllabusDataRow.createCell(6).setCellValue(120);
            syllabusDataRow.createCell(7).setCellValue(120);
            syllabusDataRow.createCell(8).setCellValue(120);
            syllabusDataRow.createCell(9).setCellValue(-20);
            syllabusDataRow.createCell(10).setCellValue(1200);
            syllabusDataRow.createCell(11).setCellValue(1000);
            syllabusDataRow.createCell(12).setCellValue("Test");
            syllabusDataRow.createCell(13).setCellValue("test1");
            syllabusDataRow.createCell(14).setCellValue("test2");
            syllabusDataRow.createCell(15).setCellValue("test3");
            syllabusDataRow.createCell(16).setCellValue("test4");
            // Add more data as needed for SyllabusData

            // TrainingData Sheet
            Sheet trainingSheet = workbook.createSheet("TrainingData");

            // Create header row for TrainingData
            Row trainingHeaderRow = trainingSheet.createRow(0);
            trainingHeaderRow.createCell(0).setCellValue("Day");
            trainingHeaderRow.createCell(1).setCellValue("Unit Number");
            trainingHeaderRow.createCell(2).setCellValue("Unit Name");
            trainingHeaderRow.createCell(3).setCellValue("Content Number");
            trainingHeaderRow.createCell(4).setCellValue("Content Name");
            trainingHeaderRow.createCell(5).setCellValue("Output Standards");
            trainingHeaderRow.createCell(6).setCellValue("Duration (minutes)");
            trainingHeaderRow.createCell(7).setCellValue("Delivery Type");
            trainingHeaderRow.createCell(8).setCellValue("Method");
            // Add more headers as needed for TrainingData

            // Create data row for TrainingData
            Row trainingDataRow = trainingSheet.createRow(1);
            trainingDataRow.createCell(0).setCellValue(1);
            trainingDataRow.createCell(1).setCellValue(1);
            trainingDataRow.createCell(2).setCellValue("Test Unit Name");
            trainingDataRow.createCell(3).setCellValue(1);
            trainingDataRow.createCell(4).setCellValue("Test Content Name");
            trainingDataRow.createCell(5).setCellValue("H4SD, H5SD");
            trainingDataRow.createCell(6).setCellValue(30);
            trainingDataRow.createCell(7).setCellValue("Concept/Lecture");
            trainingDataRow.createCell(8).setCellValue("Online");

            // Add more data as needed for TrainingData

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] inValidateMockExcelData2() {
        try (Workbook workbook = new XSSFWorkbook()) {
            // SyllabusData Sheet
            Sheet syllabusSheet = workbook.createSheet("SyllabusData");

            // Create header row for SyllabusData
            Row syllabusHeaderRow = syllabusSheet.createRow(0);
            syllabusHeaderRow.createCell(0).setCellValue("Code");
            syllabusHeaderRow.createCell(1).setCellValue("Name");
            syllabusHeaderRow.createCell(2).setCellValue("Version");
            syllabusHeaderRow.createCell(3).setCellValue("Course Object");
            syllabusHeaderRow.createCell(4).setCellValue("Level");
            syllabusHeaderRow.createCell(5).setCellValue("Technical Requirements");
            syllabusHeaderRow.createCell(6).setCellValue("Quiz");
            syllabusHeaderRow.createCell(7).setCellValue("Assignment");
            syllabusHeaderRow.createCell(8).setCellValue("Final Practice");
            syllabusHeaderRow.createCell(9).setCellValue("Final Exam");
            syllabusHeaderRow.createCell(10).setCellValue("Final Theory");
            syllabusHeaderRow.createCell(11).setCellValue("GPA criteria");
            syllabusHeaderRow.createCell(12).setCellValue("Training Principle");
            syllabusHeaderRow.createCell(13).setCellValue("Retest Principle");
            syllabusHeaderRow.createCell(14).setCellValue("Marking Principle");
            syllabusHeaderRow.createCell(15).setCellValue("Waiver Criteria Principle");
            syllabusHeaderRow.createCell(16).setCellValue("Others Principle");

            Row syllabusDataRow = syllabusSheet.createRow(1);
            syllabusDataRow.createCell(0).setCellValue("SYL001");
            syllabusDataRow.createCell(1).setCellValue("Syllabus 1");
            syllabusDataRow.createCell(2).setCellValue(1);
            syllabusDataRow.createCell(3).setCellValue("Test Object");
            syllabusDataRow.createCell(4).setCellValue("Beginner");
            syllabusDataRow.createCell(5).setCellValue("TR test");
            syllabusDataRow.createCell(6).setCellValue(40);
            syllabusDataRow.createCell(7).setCellValue(30);
            syllabusDataRow.createCell(8).setCellValue(30);
            syllabusDataRow.createCell(9).setCellValue(40);
            syllabusDataRow.createCell(10).setCellValue(70);
            syllabusDataRow.createCell(11).setCellValue(60);
            syllabusDataRow.createCell(12).setCellValue("Test");
            syllabusDataRow.createCell(13).setCellValue("test1");
            syllabusDataRow.createCell(14).setCellValue("test2");
            syllabusDataRow.createCell(15).setCellValue("test3");
            syllabusDataRow.createCell(16).setCellValue("test4");
            // Add more data as needed for SyllabusData

            // TrainingData Sheet
            Sheet trainingSheet = workbook.createSheet("TrainingData");

            // Create header row for TrainingData
            Row trainingHeaderRow = trainingSheet.createRow(0);
            trainingHeaderRow.createCell(0).setCellValue("Day");
            trainingHeaderRow.createCell(1).setCellValue("Unit Number");
            trainingHeaderRow.createCell(2).setCellValue("Unit Name");
            trainingHeaderRow.createCell(3).setCellValue("Content Number");
            trainingHeaderRow.createCell(4).setCellValue("Content Name");
            trainingHeaderRow.createCell(5).setCellValue("Output Standards");
            trainingHeaderRow.createCell(6).setCellValue("Duration (minutes)");
            trainingHeaderRow.createCell(7).setCellValue("Delivery Type");
            trainingHeaderRow.createCell(8).setCellValue("Method");
            // Add more headers as needed for TrainingData

            // Create data row for TrainingData
            Row trainingDataRow = trainingSheet.createRow(1);
            trainingDataRow.createCell(0).setCellValue(1);
            trainingDataRow.createCell(1).setCellValue(1);
            trainingDataRow.createCell(2).setCellValue("Test Unit Name");
            trainingDataRow.createCell(3).setCellValue(1);
            trainingDataRow.createCell(4).setCellValue("Test Content Name");
            trainingDataRow.createCell(5).setCellValue("H4SD, H5SD");
            trainingDataRow.createCell(6).setCellValue(30);
            trainingDataRow.createCell(7).setCellValue("Concept/Lecture");
            trainingDataRow.createCell(8).setCellValue("Online");

            // Add more data as needed for TrainingData

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] inValidateMockExcelData3() {
        try (Workbook workbook = new XSSFWorkbook()) {
            // SyllabusData Sheet
            Sheet syllabusSheet = workbook.createSheet("SyllabusData");

            // Create header row for SyllabusData
            Row syllabusHeaderRow = syllabusSheet.createRow(0);
            syllabusHeaderRow.createCell(0).setCellValue("Code");
            syllabusHeaderRow.createCell(1).setCellValue("Name");
            syllabusHeaderRow.createCell(2).setCellValue("Version");
            syllabusHeaderRow.createCell(3).setCellValue("Course Object");
            syllabusHeaderRow.createCell(4).setCellValue("Level");
            syllabusHeaderRow.createCell(5).setCellValue("Technical Requirements");
            syllabusHeaderRow.createCell(6).setCellValue("Quiz");
            syllabusHeaderRow.createCell(7).setCellValue("Assignment");
            syllabusHeaderRow.createCell(8).setCellValue("Final Practice");
            syllabusHeaderRow.createCell(9).setCellValue("Final Exam");
            syllabusHeaderRow.createCell(10).setCellValue("Final Theory");
            syllabusHeaderRow.createCell(11).setCellValue("GPA criteria");
            syllabusHeaderRow.createCell(12).setCellValue("Training Principle");
            syllabusHeaderRow.createCell(13).setCellValue("Retest Principle");
            syllabusHeaderRow.createCell(14).setCellValue("Marking Principle");
            syllabusHeaderRow.createCell(15).setCellValue("Waiver Criteria Principle");
            syllabusHeaderRow.createCell(16).setCellValue("Others Principle");

            Row syllabusDataRow = syllabusSheet.createRow(1);
            syllabusDataRow.createCell(0).setCellValue("SYL001");
            syllabusDataRow.createCell(1).setCellValue("Syllabus 1");
            syllabusDataRow.createCell(2).setCellValue(1);
            syllabusDataRow.createCell(3).setCellValue("Test Object");
            syllabusDataRow.createCell(4).setCellValue("Beginner");
            syllabusDataRow.createCell(5).setCellValue("TR test");
            syllabusDataRow.createCell(6).setCellValue(30);
            syllabusDataRow.createCell(7).setCellValue(30);
            syllabusDataRow.createCell(8).setCellValue(30);
            syllabusDataRow.createCell(9).setCellValue(40);
            syllabusDataRow.createCell(10).setCellValue(90);
            syllabusDataRow.createCell(11).setCellValue(60);
            syllabusDataRow.createCell(12).setCellValue("Test");
            syllabusDataRow.createCell(13).setCellValue("test1");
            syllabusDataRow.createCell(14).setCellValue("test2");
            syllabusDataRow.createCell(15).setCellValue("test3");
            syllabusDataRow.createCell(16).setCellValue("test4");
            // Add more data as needed for SyllabusData

            // TrainingData Sheet
            Sheet trainingSheet = workbook.createSheet("TrainingData");

            // Create header row for TrainingData
            Row trainingHeaderRow = trainingSheet.createRow(0);
            trainingHeaderRow.createCell(0).setCellValue("Day");
            trainingHeaderRow.createCell(1).setCellValue("Unit Number");
            trainingHeaderRow.createCell(2).setCellValue("Unit Name");
            trainingHeaderRow.createCell(3).setCellValue("Content Number");
            trainingHeaderRow.createCell(4).setCellValue("Content Name");
            trainingHeaderRow.createCell(5).setCellValue("Output Standards");
            trainingHeaderRow.createCell(6).setCellValue("Duration (minutes)");
            trainingHeaderRow.createCell(7).setCellValue("Delivery Type");
            trainingHeaderRow.createCell(8).setCellValue("Method");
            // Add more headers as needed for TrainingData

            // Create data row for TrainingData
            Row trainingDataRow = trainingSheet.createRow(1);
            trainingDataRow.createCell(0).setCellValue(1);
            trainingDataRow.createCell(1).setCellValue(1);
            trainingDataRow.createCell(2).setCellValue("Test Unit Name");
            trainingDataRow.createCell(3).setCellValue(1);
            trainingDataRow.createCell(4).setCellValue("Test Content Name");
            trainingDataRow.createCell(5).setCellValue("H4SD, H5SD");
            trainingDataRow.createCell(6).setCellValue(30);
            trainingDataRow.createCell(7).setCellValue("Concept/Lecture");
            trainingDataRow.createCell(8).setCellValue("Online");

            // Add more data as needed for TrainingData

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
