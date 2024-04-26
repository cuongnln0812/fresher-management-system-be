package com.example.phase1_fams.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import com.example.phase1_fams.dto.*;
import com.example.phase1_fams.dto.exception.ResourceNotFoundException;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.SyllabusConverter;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.SyllabusReq;
import com.example.phase1_fams.dto.response.DaysUnitRes;
import com.example.phase1_fams.dto.response.SyllabusDetailsRes;
import com.example.phase1_fams.dto.response.TrainingContentRes;
import com.example.phase1_fams.dto.response.TrainingUnitRes;
import com.example.phase1_fams.service.impl.SyllabusServiceImpl;
import com.example.phase1_fams.dto.request.SyllabusReqUpdate;
import com.example.phase1_fams.dto.response.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class SyllabusServiceTests {
    @Mock
    private SyllabusRepository syllabusRepository;
    @Mock
    private DaysUnitRepository daysUnitRepository;
    @Mock
    private TrainingUnitRepository trainingUnitRepository;
    @Mock
    private TrainingContentRepository trainingContentRepository;
    @Mock
    private LearningObjectiveRepository learningObjectiveRepository;
    @Mock
    private LearningMaterialRepository learningMaterialRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SyllabusConverter syllabusConverter;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private LearningMaterialService learningMaterialService;
    @InjectMocks
    private SyllabusServiceImpl syllabusService;

    SyllabusReq testSyllabusReq;
    SyllabusGeneralDTO testSyllabusGeneralDTO;
    SyllabusOutlineDTO testSyllabusOutlineDTO;
    SyllabusOthersDTO testSyllabusOthersDTO;
    DaysUnitDTO testDaysUnitDTO;
    Set<DaysUnitDTO> testDaysUnitDTOSet = new HashSet<>();
    TrainingUnitDTO testTrainingUnitDTO;
    Set<TrainingUnitDTO> testTrainingUnitDTOSet = new HashSet<>();
    TrainingContentDTO testTrainingContentDTO;
    Set<TrainingContentDTO> testTrainingContentDTOSet = new HashSet<>();
    Set<String> outputStandard = new HashSet<>();
    AssessmentSchemeDTO testAssessmentSchemeDTO;
    TrainingPrincipleDTO testTrainingPrincipleDTO;
    Syllabus testSyllabus;
    DaysUnit testDayUnit;
    TrainingUnit testTrainingUnit;
    TrainingContent testTrainingContent;
    LearningObjective testLearningObjective1;
    LearningObjective testLearningObjective2;
    Set<DaysUnit> daysUnitSet = new HashSet<>();
    Set<TrainingUnit> trainingUnitSet = new HashSet<>();
    Set<TrainingContent> trainingContentSet = new HashSet<>();
    Set<LearningObjective> learningObjectiveSet = new HashSet<>();

    Syllabus syllabus1;

    Syllabus syllabus2;


    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init(){
        syllabus1 = Syllabus.builder()
                .code("PRN211")
                .name("Basic Programming With C#")
                .createdDate(LocalDate.of(2024, Calendar.FEBRUARY, 23))
                .createdBy("admin")
                .status(1)
                .build();
        syllabus2 = Syllabus.builder()
                .code("PRO192")
                .name("OOP With Java")
                .createdDate(LocalDate.of(2024, Calendar.FEBRUARY, 23))
                .createdBy("admin")
                .status(1)
                .build();

        testSyllabusGeneralDTO = SyllabusGeneralDTO.builder()
                .level("Intermediate")
                .technicalRequirements("Java 11, IDE")
                .courseObjectives("Understand advanced Java concepts")
                .attendeeNumber(50)
                .build();

        outputStandard.add("H4SD");
        outputStandard.add("H5SD");

        testTrainingContentDTO = TrainingContentDTO.builder()
                .orderNumber(1)
                .contentName("Java Data Types")
                .duration(60)
                .deliveryType("Offline")
                .method("Concept/Lecture")
                .outputStandards(outputStandard)
                .build();

        testTrainingContentDTOSet.add(testTrainingContentDTO);

        testTrainingUnitDTO = TrainingUnitDTO.builder()
                .unitNumber(1)
                .unitName("Introduction to Java")
                .trainingTime(1.0F)
                .trainingContents(testTrainingContentDTOSet)// Assuming time is in minutes
                .build();

        testTrainingUnitDTOSet.add(testTrainingUnitDTO);

        testDaysUnitDTO = DaysUnitDTO.builder()
                .dayNumber(1)
                .trainingUnits(testTrainingUnitDTOSet)
                .build();

        testDaysUnitDTOSet.add(testDaysUnitDTO);

        testSyllabusOutlineDTO = SyllabusOutlineDTO.builder()
                .days(testDaysUnitDTOSet)
                .build();

        testAssessmentSchemeDTO = AssessmentSchemeDTO.builder()
                .quiz(25)
                .assignment(25)
                ._final(50)
                .finalTheory(40)
                .finalPractice(60)
                .gpa(60)
                .build();

        testTrainingPrincipleDTO = TrainingPrincipleDTO.builder()
                .training("Online")
                .retest("Available upon request")
                .marking("Standard")
                .waiverCriteria("Not applicable")
                .others("N/A")
                .build();

        testSyllabusOthersDTO = SyllabusOthersDTO.builder()
                .assessmentScheme(testAssessmentSchemeDTO)
                .trainingDeliveryPrinciple(testTrainingPrincipleDTO)
                .build();
        testSyllabusReq = SyllabusReq.builder()
                .code("SYL2024")
                .syllabusName("Advanced Java")
                .syllabusGeneral(testSyllabusGeneralDTO)
                .syllabusOutline(testSyllabusOutlineDTO)
                .syllabusOthers(testSyllabusOthersDTO)
                .build();

        testSyllabus = Syllabus.builder()
                .code("SYL2024")
                .name("Advanced Java")
                .level("Intermediate")
                .version("1.0.0")
                .i1(1)
                .i2(0)
                .i3(0)
                .attendeeNumber(50)
                .technicalRequirements("Java 11, IDE")
                .courseObjectives("Understand advanced Java concepts")
                .quizAssessment(25)
                .assignmentAssessment(25)
                .finalAssessment(50)
                .finalTheoryAssessment(40)
                .finalPracticeAssessment(60)
                .gpaCriteria(60)
                .trainingPrinciple("Online")
                .reTestPrinciple("Available upon request")
                .markingPrinciple("Standard")
                .waiverCriteriaPrinciple("Not applicable")
                .othersPrinciple("N/A")
                .daysUnits(daysUnitSet)
                .trainingProgramSyllabusSet(new HashSet<>())
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();

        testDayUnit = DaysUnit.builder()
                .id(1L)
                .dayNumber(1)
                .trainingUnits(trainingUnitSet)
                .syllabus(testSyllabus) // Assuming Syllabus needs to be set externally or is not needed for the test
                .build();

        testTrainingUnit = TrainingUnit.builder()
                .id(1L)
                .unitNumber(1)
                .unitName("Introduction to Java")
                .trainingTime(1.0F) // Assuming time is in minutes
                .trainingContents(trainingContentSet)
                .daysUnit(testDayUnit) // Assuming DaysUnit needs to be set externally or is not needed for this test data
                .build();

        testTrainingContent = TrainingContent.builder()
                .id(1L)
                .orderNumber(1)
                .name("Java Data Types")
                .duration(60)
                .deliveryType("Offline")
                .method("Concept/Lecture")
                .objectiveCodes(learningObjectiveSet)
                .trainingUnit(testTrainingUnit)
                .learningMaterials(new HashSet<>())
                .build();

        testLearningObjective1 = LearningObjective.builder()
                .code("H4SD")
                .name("Understanding Java Generics")
                .type("Technical")
                .description("Learn how to use Generics in Java for type-safe collections.")
                .build();
        testLearningObjective2 = LearningObjective.builder()
                .code("H5SD")
                .name("Understanding OOP Concepts")
                .type("Technical")
                .description("Learn how to know OOP in coding.")
                .build();

        daysUnitSet.add(testDayUnit);
        trainingUnitSet.add(testTrainingUnit);
        trainingContentSet.add(testTrainingContent);
        learningObjectiveSet.add(testLearningObjective1);
        learningObjectiveSet.add(testLearningObjective2);
    }

    @Test
    void createSyllabusAsActive_Success() {
        // Arrange
        when(authenticationService.getName()).thenReturn("MockUser");
        when(syllabusRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(learningObjectiveRepository.findByCode(anyString())).thenReturn(Optional.empty()); // Default case
        when(learningObjectiveRepository.findByCode(testLearningObjective1.getCode())).thenReturn(Optional.of(testLearningObjective1));
        when(learningObjectiveRepository.findByCode(testLearningObjective2.getCode())).thenReturn(Optional.of(testLearningObjective2));
        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingContentRepository.save(any(TrainingContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(syllabusConverter.convertToDetailsRes(any(Syllabus.class))).thenAnswer(invocation -> {
            Syllabus newSyllabus = invocation.getArgument(0);
            return mapToDetailRes(newSyllabus);
        });

        // Act
        SyllabusDetailsRes result = syllabusService.createSyllabusAsActive(testSyllabusReq);

        // Assert
        assertNotNull(result);
        verify(syllabusRepository, times(2)).save(any(Syllabus.class)); // Verify save is called
        verify(syllabusConverter, times(1)).convertToDetailsRes(any(Syllabus.class)); // Verify convert is called
        assertNotNull(result.getCode());

        assertEquals(testSyllabusReq.getCode(), result.getCode(), "The syllabus codes should match.");
        assertEquals(testSyllabusReq.getSyllabusName(), result.getSyllabusName(), "The syllabus names should match.");
        assertEquals(1, result.getStatus(), "The status should indicate an active syllabus.");
        assertEquals("1.0.0", result.getVersion(), "The version should be set correctly to 1.0.0.");
        // Basic properties of SyllabusDetailsRes
        assertBasicPropertiesOfDetailRes(result);
        assertEquals("MockUser", result.getCreatedBy(), "The creator should match the mocked user.");
        // For created and modified dates, you might want to check if they're not null since their exact value might be hard to predict
        assertNotNull(result.getCreatedDate(), "The created date should be set.");
    }

    @Test
    void createSyllabusAsDraft_Success(){

        // Arrange
        when(authenticationService.getName()).thenReturn("MockUser");
        when(syllabusRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(learningObjectiveRepository.findByCode(anyString())).thenReturn(Optional.empty()); // Default case
        when(learningObjectiveRepository.findByCode(testLearningObjective1.getCode())).thenReturn(Optional.of(testLearningObjective1));
        when(learningObjectiveRepository.findByCode(testLearningObjective2.getCode())).thenReturn(Optional.of(testLearningObjective2));
        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingContentRepository.save(any(TrainingContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(syllabusConverter.convertToDetailsRes(any(Syllabus.class))).thenAnswer(invocation -> {
            Syllabus newSyllabus = invocation.getArgument(0);
            return mapToDetailRes(newSyllabus);
        });

        // Act
        SyllabusDetailsRes result = syllabusService.createSyllabusAsDraft(testSyllabusReq);

        // Assert
        assertNotNull(result);
        verify(syllabusRepository, times(2)).save(any(Syllabus.class)); // Verify save is called
        verify(syllabusConverter, times(1)).convertToDetailsRes(any(Syllabus.class)); // Verify convert is called
        assertNotNull(result.getCode());

        assertEquals(testSyllabusReq.getCode(), result.getCode(), "The syllabus codes should match.");
        assertEquals(testSyllabusReq.getSyllabusName(), result.getSyllabusName(), "The syllabus names should match.");
        assertEquals(2, result.getStatus(), "The status should indicate an active syllabus.");
        assertEquals("0.0.1", result.getVersion(), "The version should be set correctly to 0.0.1");
        // Basic properties of SyllabusDetailsRes
        assertBasicPropertiesOfDetailRes(result);
        assertEquals("MockUser", result.getCreatedBy(), "The creator should match the mocked user.");
        // For created and modified dates, you might want to check if they're not null since their exact value might be hard to predict
        assertNotNull(result.getCreatedDate(), "The created date should be set.");
    }

    @Test
    void createSyllabus_CodeExistingTrue(){
        //Setup
        when(syllabusRepository.findByCode(anyString())).thenReturn(Optional.of(new Syllabus()));
        // Given
        SyllabusReq duplicateCodeSyllabusReq = new SyllabusReq();
        duplicateCodeSyllabusReq.setCode("EXISTING_CODE");

        // When + Then
        when(syllabusRepository.findByCode(anyString())).thenReturn(Optional.of(new Syllabus()));
        ApiException exception = assertThrows(ApiException.class, () -> syllabusService.createSyllabus(duplicateCodeSyllabusReq),
                "Expected createSyllabus to throw, but it didn't");

        assertTrue(exception.getMessage().contains("Syllabus code is already in use"));
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void createSyllabusAsActive_FinalAssessmentSumIsIncorrect() {
        AssessmentSchemeDTO incorrectFinal = AssessmentSchemeDTO.builder()
                .quiz(25)
                .assignment(25)
                ._final(50)
                .finalTheory(50)
                .finalPractice(60)
                .gpa(60)
                .build();
        testSyllabusOthersDTO = SyllabusOthersDTO.builder()
                .assessmentScheme(incorrectFinal)
                .build();

        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusService.validatingSyllabusOthersField(testSyllabusOthersDTO),
                "Expected ApiException to be thrown for incorrect final assessment sum");

        assertTrue(exception.getMessage().contains("Final Assessment is not equal 100"));
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void createSyllabusAsActive_TotalAssessmentSumIsIncorrect() {
        AssessmentSchemeDTO incorrectTotal = AssessmentSchemeDTO.builder()
                .quiz(25)
                .assignment(25)
                ._final(60)
                .finalTheory(40)
                .finalPractice(60)
                .gpa(60)
                .build();
        testSyllabusOthersDTO = SyllabusOthersDTO.builder()
                .assessmentScheme(incorrectTotal)
                .build();

        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusService.validatingSyllabusOthersField(testSyllabusOthersDTO),
                "Expected ApiException to be thrown for incorrect total assessment sum");

        assertTrue(exception.getMessage().contains("Total Assessment is not equal 100"));
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void createSyllabusAsActive_InvalidAssessment() {
        AssessmentSchemeDTO invalidAssessment = AssessmentSchemeDTO.builder()
                .quiz(110)
                .assignment(25)
                ._final(50)
                .finalTheory(40)
                .finalPractice(60)
                .gpa(60)
                .build();
        testSyllabusOthersDTO = SyllabusOthersDTO.builder()
                .assessmentScheme(invalidAssessment)
                .build();

        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusService.validatingSyllabusOthersField(testSyllabusOthersDTO),
                "Expected ApiException to be thrown for incorrect total assessment sum");

        assertTrue(exception.getMessage().contains("Assessment should larger than 0 and smaller than 100"));
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

//    @Test
//    public void testGetAllSyllabusPage() {
//
//        List<Syllabus> syllabuses = new ArrayList<>();
//        syllabuses.add(testSyllabus);
//
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Syllabus> syllabusPage = new PageImpl<>(syllabuses);
//
//        when(syllabusRepository.findAll(pageable)).thenReturn(syllabusPage);
//
//        Page<SyllabusPageRes> result = syllabusService.searchSyllabus(null,0, 10);
//
//        verify(syllabusRepository).findAll(pageable);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(syllabusPage.getTotalElements(), result.getTotalElements());
//        Assertions.assertEquals(syllabusPage.getContent().size(), result.getContent().size());
//
//    }

    @Test
    public void testGetSyllabusDetails() {
        String syllabusCode = "SYL2024";

        when(syllabusRepository.findByCode(syllabusCode)).thenReturn(Optional.of(testSyllabus));
        when(syllabusConverter.convertToDetailsRes(testSyllabus)).thenAnswer(invocation ->{
            Syllabus syllabus = invocation.getArgument(0);
            return mapToDetailRes(syllabus);
        });

        SyllabusDetailsRes result = syllabusService.getSyllabusDetails(syllabusCode);

        verify(syllabusRepository, times(1)).findByCode(syllabusCode);
        assertNotNull(result);
        assertBasicPropertiesOfDetailRes(result);
    }

    @Test
    public void testGetSyllabusDetails_NotFound() {

        String syllabusCode = "NONEXISTENT";

        when(syllabusRepository.findByCode(syllabusCode)).thenReturn(Optional.empty());

        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            syllabusService.getSyllabusDetails(syllabusCode);
        });

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Assertions.assertEquals("Syllabus not found!!", exception.getMessage());
    }

    SyllabusDetailsRes mapToDetailRes(Syllabus newSyllabus){
        SyllabusDetailsRes syllabusRes = new SyllabusDetailsRes();
        syllabusRes.setCode(newSyllabus.getCode());
        syllabusRes.setSyllabusName(newSyllabus.getName());
        syllabusRes.setVersion(newSyllabus.getVersion());
        syllabusRes.getSyllabusGeneral().setLevel(newSyllabus.getLevel());
        syllabusRes.getSyllabusGeneral().setCourseObjectives(newSyllabus.getCourseObjectives());
        syllabusRes.getSyllabusGeneral().setAttendeeNumber(newSyllabus.getAttendeeNumber());
        syllabusRes.getSyllabusGeneral().setTechnicalRequirements(newSyllabus.getTechnicalRequirements());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setQuiz(newSyllabus.getQuizAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().set_final(newSyllabus.getFinalAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setGpa(newSyllabus.getGpaCriteria());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setAssignment(newSyllabus.getAssignmentAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setFinalTheory(newSyllabus.getFinalTheoryAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setFinalPractice(newSyllabus.getFinalPracticeAssessment());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setTraining(newSyllabus.getTrainingPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setRetest(newSyllabus.getReTestPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setOthers(newSyllabus.getOthersPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setMarking(newSyllabus.getMarkingPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setWaiverCriteria(newSyllabus.getWaiverCriteriaPrinciple());
        syllabusRes.setCreatedBy(newSyllabus.getCreatedBy());
        syllabusRes.setCreatedDate(newSyllabus.getCreatedDate());
        syllabusRes.setModifiedBy(newSyllabus.getModifiedBy());
        syllabusRes.setModifiedDate(newSyllabus.getModifiedDate());
        syllabusRes.setStatus(newSyllabus.getStatus());

        List<DaysUnitRes> daysUnitResSet = newSyllabus.getDaysUnits().stream()
                .map(dayUnit -> {
                    List<TrainingUnitRes> trainingUnitResSet = dayUnit.getTrainingUnits().stream()
                            .map(trainingUnit -> {
                                List<TrainingContentRes> trainingContentResSet = trainingUnit.getTrainingContents().stream()
                                        .map(trainingContent -> {
                                            List<String> objCodes = trainingContent.getObjectiveCodes().stream()
                                                    .map(LearningObjective::getCode).toList();
                                                        List<LearningMaterialDto> learningMaterialDtoList = trainingContent.getLearningMaterials().stream()
                                                                .map(learningMaterial -> new LearningMaterialDto(learningMaterial.getId(), learningMaterial.getFileName(),
                                                                        learningMaterial.getFileType(), learningMaterialService.generateUrl(learningMaterial.getFileName(), HttpMethod.GET, trainingContent.getId()),
                                                                        learningMaterial.getUploadBy(), learningMaterial.getUploadDate()))
                                                                .toList();
                                            return new TrainingContentRes(trainingContent.getId(), trainingContent.getOrderNumber(),
                                                    trainingContent.getName(), trainingContent.getDuration(), objCodes,
                                                    trainingContent.getDeliveryType(), trainingContent.getMethod(), learningMaterialDtoList);
                                        })
                                        .toList();
                                return new TrainingUnitRes(trainingUnit.getId(), trainingUnit.getUnitNumber(),
                                        trainingUnit.getUnitName(), trainingUnit.getTrainingTime(), trainingContentResSet);
                            })
                            .toList();
                    return new DaysUnitRes(dayUnit.getId(), dayUnit.getDayNumber(), trainingUnitResSet);
                })
                .toList();
        syllabusRes.getSyllabusOutline().setDays(daysUnitResSet);
        return syllabusRes;
    }

    void assertBasicPropertiesOfDetailRes(SyllabusDetailsRes result){
        // Properties within SyllabusGeneral part of SyllabusDetailsRes
        assertEquals(testSyllabusReq.getSyllabusGeneral().getLevel(), result.getSyllabusGeneral().getLevel(), "The levels should match.");
        assertEquals(testSyllabusReq.getSyllabusGeneral().getAttendeeNumber(), result.getSyllabusGeneral().getAttendeeNumber(), "The attendee numbers should match.");
        assertEquals(testSyllabusReq.getSyllabusGeneral().getTechnicalRequirements(), result.getSyllabusGeneral().getTechnicalRequirements(), "The technical requirements should match.");
        assertEquals(testSyllabusReq.getSyllabusGeneral().getCourseObjectives(), result.getSyllabusGeneral().getCourseObjectives(), "The course objectives should match.");

        // Assertions for assessment scheme within SyllabusOthers part of SyllabusDetailsRes
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getQuiz(), result.getSyllabusOthers().getAssessmentScheme().getQuiz(), "The quiz assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getAssignment(), result.getSyllabusOthers().getAssessmentScheme().getAssignment(), "The assignment assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().get_final(), result.getSyllabusOthers().getAssessmentScheme().get_final(), "The final assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getGpa(), result.getSyllabusOthers().getAssessmentScheme().getGpa(), "The GPA criteria values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalPractice(), result.getSyllabusOthers().getAssessmentScheme().getFinalPractice(), "The final practice assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalTheory(), result.getSyllabusOthers().getAssessmentScheme().getFinalTheory(), "The final theory assessment values should match.");

        // Assertions for training delivery principles within SyllabusOthers part of SyllabusDetailsRes
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getTraining(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getTraining(), "The training principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getRetest(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getRetest(), "The retest principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getMarking(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getMarking(), "The marking principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getWaiverCriteria(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getWaiverCriteria(), "The waiver criteria principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getOthers(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getOthers(), "The others principles should match.");

        // Assert that the syllabus outline is correctly set
        assertNotNull(result.getSyllabusOutline(), "The syllabus outline should not be null.");

        // Assert the correct number of days units
        assertEquals(testSyllabusReq.getSyllabusOutline().getDays().size(), result.getSyllabusOutline().getDays().size(), "The number of days units should match.");

        // Asserting properties of DaysUnits, TrainingUnits, and TrainingContents
        for (DaysUnitDTO expectedDayUnit : testSyllabusReq.getSyllabusOutline().getDays()) {
            DaysUnitRes actualDayUnit = result.getSyllabusOutline().getDays().stream()
                    .filter(actualDay -> actualDay.getDayNumber() == expectedDayUnit.getDayNumber())
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Expected day number not found: " + expectedDayUnit.getDayNumber()));

            // Now for each DaysUnit, assert the properties of TrainingUnits
            Set<TrainingUnitDTO> expectedTrainingUnits = expectedDayUnit.getTrainingUnits();
            List<TrainingUnitRes> actualTrainingUnits = actualDayUnit.getTrainingUnits();

            assertEquals(expectedTrainingUnits.size(), actualTrainingUnits.size(), "The number of training units should match for day " + expectedDayUnit.getDayNumber());

            for (TrainingUnitDTO expectedTrainingUnit : expectedTrainingUnits) {
                TrainingUnitRes actualTrainingUnit = actualTrainingUnits.stream()
                        .filter(actualUnit -> actualUnit.getUnitNumber() == expectedTrainingUnit.getUnitNumber() && actualUnit.getUnitName().equals(expectedTrainingUnit.getUnitName()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Expected training unit not found: " + expectedTrainingUnit.getUnitName()));

                // Assert specific properties of TrainingUnit here, if any
                assertEquals(expectedTrainingUnit.getTrainingTime(), actualTrainingUnit.getTrainingTime(), "Duration should match for training unit: " + expectedTrainingUnit.getUnitName());

                // Now, for each TrainingUnit, assert the properties of TrainingContents
                Set<TrainingContentDTO> expectedTrainingContents = expectedTrainingUnit.getTrainingContents();
                List<TrainingContentRes> actualTrainingContents = actualTrainingUnit.getTrainingContents();

                assertEquals(expectedTrainingContents.size(), actualTrainingContents.size(), "The number of training contents should match for training unit " + expectedTrainingUnit.getUnitName());

                for (TrainingContentDTO expectedTrainingContent : expectedTrainingContents) {
                    TrainingContentRes actualTrainingContent = actualTrainingContents.stream()
                            .filter(actualContent -> actualContent.getOrderNumber() == expectedTrainingContent.getOrderNumber() && actualContent.getContentName().equals(expectedTrainingContent.getContentName()))
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("Expected training content not found: " + expectedTrainingContent.getContentName()));

                    // Assert specific properties of TrainingContent here, such as duration, deliveryType, and method
                    assertEquals(expectedTrainingContent.getDuration(), actualTrainingContent.getDuration(), "Duration should match for content: " + expectedTrainingContent.getContentName());
                    assertEquals(expectedTrainingContent.getDeliveryType(), actualTrainingContent.getDeliveryType(), "Delivery type should match for content: " + expectedTrainingContent.getContentName());
                    assertEquals(expectedTrainingContent.getMethod(), actualTrainingContent.getMethod(), "Method should match for content: " + expectedTrainingContent.getContentName());
                    // If you need to assert the objective codes
                    Set<String> actualOutputStandards = new HashSet<>(actualTrainingContent.getOutputStandards());
                    assertEquals(expectedTrainingContent.getOutputStandards(), actualOutputStandards, "Objective codes should match for content: " + expectedTrainingContent.getContentName());
                }
            }
        }
    }


//    @Test
//    void shouldSuccessfullySearchSyllabusWithSearchKeyIsNotNull() {
//
//        String searchKey = "active";
//        int page = 0;
//        int size = 1;
//        Pageable pageable = PageRequest.of(page, size);
//        String normalizeKey = "1";
//        List<Syllabus> syllabus = Arrays.asList(syllabus1, syllabus2);
//        Page<Syllabus> syllabusPage = new PageImpl<Syllabus>(syllabus);
//        List<String> outputStandard = new ArrayList<>();
//        outputStandard.add("os1");
//        outputStandard.add("os2");
//        SyllabusPageRes syllabusDTO1 = new SyllabusPageRes(
//                "Basic Programming With C#",
//                "PRN211",
//                LocalDate.of(2024, Calendar.FEBRUARY, 23),
//                "admin",
//                outputStandard,
//                10,
//                1
//        );
//        SyllabusPageRes syllabusDTO2 = new SyllabusPageRes(
//                "OOP With Java",
//                "PRO192",
//                LocalDate.of(2024, Calendar.FEBRUARY, 23),
//                "admin",
//                outputStandard,
//                10,
//                1
//        );
//        List<SyllabusPageRes> syllabusListDTO = new ArrayList<>();
//        syllabusListDTO.add(syllabusDTO1);
//        syllabusListDTO.add(syllabusDTO2);
//        Page<SyllabusPageRes> syllabusPageRes = new PageImpl<SyllabusPageRes>(syllabusListDTO);
//
//        when(syllabusRepository.searchAcrossFields(normalizeKey, pageable))
//                .thenReturn(syllabusPage);
//
//        Page<SyllabusPageRes> syllabusRes = syllabusService.searchSyllabus(searchKey, page, size);
//
//        assertNotNull(syllabusRes);
//        assertEquals(syllabusPageRes.getSize(), syllabusRes.getSize());
//
//        verify(syllabusRepository, times(1))
//                .searchAcrossFields(normalizeKey, pageable);
//
//
//    }
//
//    @Test
//    void shouldGetAllSyllabusesSearchSyllabusWithSearchKeyIsNull() {
//
//        String searchKey = null;
//        int page = 0;
//        int size = 1;
//        Pageable pageable = PageRequest.of(page, size);
//        String normalizeKey = "1";
//        List<Syllabus> syllabus = Arrays.asList(syllabus1, syllabus2);
//        Page<Syllabus> syllabusPage = new PageImpl<Syllabus>(syllabus);
//        List<String> outputStandard = new ArrayList<>();
//        outputStandard.add("os1");
//        outputStandard.add("os2");
//        SyllabusPageRes syllabusDTO1 = new SyllabusPageRes(
//                "Basic Programming With C#",
//                "PRN211",
//                LocalDate.of(2024, Calendar.FEBRUARY, 23),
//                "admin",
//                outputStandard,
//                10,
//                1
//        );
//        SyllabusPageRes syllabusDTO2 = new SyllabusPageRes(
//                "OOP With Java",
//                "PRO192",
//                LocalDate.of(2024, Calendar.FEBRUARY, 23),
//                "admin",
//                outputStandard,
//                10,
//                1
//        );
//        List<SyllabusPageRes> syllabusListDTO = new ArrayList<>();
//        syllabusListDTO.add(syllabusDTO1);
//        syllabusListDTO.add(syllabusDTO2);
//        Page<SyllabusPageRes> syllabusPageRes = new PageImpl<SyllabusPageRes>(syllabusListDTO);
//
//        when(syllabusRepository.findAll(pageable))
//                .thenReturn(syllabusPage);
//
//        Page<SyllabusPageRes> syllabusRes = syllabusService.searchSyllabus(searchKey, page, size);
//
//        assertNotNull(syllabusRes);
//        assertEquals(syllabusPageRes.getSize(), syllabusRes.getSize());
//
//        verify(syllabusRepository, times(1))
//                .findAll(pageable);
//    }

    @Test
    public void testDeactivateSyllabus_Successful() {
        // Arrange
        String code = "validCode";
        Syllabus originalSyllabus = new Syllabus();
        originalSyllabus.setStatus(1); // assuming 1 is active and 0 is inactive
        Syllabus deactivatedSyllabus = new Syllabus();
        deactivatedSyllabus.setStatus(0);

        SyllabusDetailsRes expectedResponse = new SyllabusDetailsRes(); // populate as necessary

        when(syllabusRepository.findByCode(code)).thenReturn(Optional.of(originalSyllabus));
        when(syllabusRepository.save(any(Syllabus.class))).thenReturn(deactivatedSyllabus);
        when(syllabusConverter.convertToDetailsRes(deactivatedSyllabus)).thenReturn(expectedResponse);

        // Act
        SyllabusDetailsRes result = syllabusService.deactiveSyllabus(code);

        // Assert
        assertEquals(expectedResponse, result);
        verify(syllabusRepository).findByCode(code);
        verify(syllabusRepository).save(any(Syllabus.class));
        verify(syllabusConverter).convertToDetailsRes(deactivatedSyllabus);
    }

    @Test
    public void deactiveSyllabus_ShouldThrowException_WhenSyllabusNotFound() {
        // Arrange
        String invalidCode = "invalidCode";
        when(syllabusRepository.findByCode(invalidCode)).thenReturn(Optional.empty());

        // Assert is handled by the expected exception
        ApiException exception = assertThrows(ApiException.class,
                () -> syllabusService.deactiveSyllabus(invalidCode),
                "Expected ApiException to be thrown for syllabus not found");

        assertEquals("Syllabus not found to de-activated!!", exception.getMessage());
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void should_successfully_duplicateSyllabus_success() {
        // Mock data
        Syllabus originalSyllabus = new Syllabus();
        originalSyllabus.setCode("ORIGINAL_CODE");
        // Assuming other necessary fields are set for originalSyllabus

        // Mock repository behavior
        when(authenticationService.getName()).thenReturn("MockUser");
        when(syllabusRepository.findByCode(anyString())).thenReturn(Optional.of(testSyllabus));
        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingContentRepository.save(any(TrainingContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(syllabusConverter.convertToDetailsRes(any(Syllabus.class))).thenAnswer(invocation -> {
            Syllabus newSyllabus = invocation.getArgument(0);
            return mapToDetailRes(newSyllabus);
        });
        // Mock authentication service behavior
        when(authenticationService.getName()).thenReturn("MockUser");

        // Invoke the method
        SyllabusDetailsRes result = syllabusService.duplicateSyllabus("ORIGINAL_CODE");

        // Assert
        assertNotNull(result);
        verify(syllabusRepository, times(1)).save(any(Syllabus.class)); // Verify save is called
        verify(syllabusConverter, times(1)).convertToDetailsRes(any(Syllabus.class)); // Verify convert is called
        assertNotNull(result.getCode());

        assertEquals(testSyllabus.getCode() + "_1", result.getCode(), "The syllabus codes should match.");
        assertEquals(testSyllabus.getName(), result.getSyllabusName(), "The syllabus names should match.");
        assertEquals(2, result.getStatus(), "The status should indicate a draft syllabus.");
        assertEquals("0.0.1", result.getVersion(), "The version should be set correctly to 0.0.1.");
        // Basic properties of SyllabusDetailsRes
        assertBasicPropertiesOfDetailRes(result);
        assertEquals("MockUser", result.getCreatedBy(), "The creator should match the mocked user.");
        // For created and modified dates, you might want to check if they're not null since their exact value might be hard to predict
        assertNotNull(result.getCreatedDate(), "The created date should be set.");
    }

    @Test
    void should_successfully_duplicateSyllabus_NotFound() {
        // Mock repository behavior
        when(syllabusRepository.findByCode("NON_EXISTENT_CODE")).thenReturn(Optional.empty());

        // Invoke the method
        Exception exception = assertThrows(Exception.class, () -> syllabusService.duplicateSyllabus("NON_EXISTENT_CODE"));

        // Assertion
        assertTrue(exception.getMessage().contains("Chosen syllabus not found"));
    }

//    @Test
//    void should_successfully_duplicateDayUnit() {
//        // Create a mock original DaysUnit
//        DaysUnit originalDaysUnit = new DaysUnit();
//        originalDaysUnit.setId(1L);
//        originalDaysUnit.setDayNumber(1);
//
//        // Create mock TrainingUnits and associate them with original DaysUnit
//        Set<TrainingUnit> originalTrainingUnits = new HashSet<>();
//        TrainingUnit trainingUnit1 = new TrainingUnit();
//        trainingUnit1.setId(1L);
//        originalTrainingUnits.add(trainingUnit1);
//        TrainingUnit trainingUnit2 = new TrainingUnit();
//        trainingUnit2.setId(2L);
//        originalTrainingUnits.add(trainingUnit2);
//        originalDaysUnit.setTrainingUnits(originalTrainingUnits);
//
//        // Mock the behavior of daysUnitRepository.save to return null (for testing purposes)
//        when(daysUnitRepository.save(originalDaysUnit)).thenReturn(null);
//
//        // Call the method to be tested
//        DaysUnit duplicatedDaysUnit = syllabusService.duplicateDayUnit(originalDaysUnit);
//
//        // Verify that duplicatedDaysUnit is null
//        assertEquals(null, duplicatedDaysUnit, "Duplicated DaysUnit should be null as it couldn't be saved");
//
//    }
//    @Test
//    void should_successfully_duplicateTrainingContent() {
//        // Create a mock original TrainingContent
//        TrainingContent originalTrainingContent = new TrainingContent();
//        originalTrainingContent.setOrderNumber(1);
//        originalTrainingContent.setName("Original Name");
//        originalTrainingContent.setDuration(60);
//        originalTrainingContent.setDeliveryType("Online");
//        originalTrainingContent.setMethod("Video");
//
//        // Set up mock behavior for trainingContentRepository.save
//        when(trainingContentRepository.save(any(TrainingContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Call the method to be tested
//        TrainingContent duplicatedTrainingContent = syllabusService.duplicateTrainingContent(originalTrainingContent);
//
//        // Verify that duplicatedTrainingContent is not null
//        assertNotNull(duplicatedTrainingContent, "Duplicated TrainingContent should not be null");
//
//        // Verify that attributes are correctly copied
//        assertEquals(originalTrainingContent.getOrderNumber(), duplicatedTrainingContent.getOrderNumber());
//        assertEquals(originalTrainingContent.getName(), duplicatedTrainingContent.getName());
//        assertEquals(originalTrainingContent.getDuration(), duplicatedTrainingContent.getDuration());
//        assertEquals(originalTrainingContent.getDeliveryType(), duplicatedTrainingContent.getDeliveryType());
//        assertEquals(originalTrainingContent.getMethod(), duplicatedTrainingContent.getMethod());
//        assertEquals(originalTrainingContent.getObjectiveCodes(), duplicatedTrainingContent.getObjectiveCodes());
//    }
//



    @Test
    public void testUpdateSyllabusAsDraft_Success(){

        // Arrange
        // Set SyllabusReqUpdate
        SyllabusReqUpdate syllabusReqUpdate = new SyllabusReqUpdate();
        syllabusReqUpdate.setCode("SYL2024");
        syllabusReqUpdate.setSyllabusName("Advanced Java");
        syllabusReqUpdate.setVersion(2);

        // Set syllabusGeneral
        syllabusReqUpdate.setSyllabusGeneral(new SyllabusGeneralDTO("Intermediate", 20,
                "Technical requirements", "Course objectives"));

        // Set syllabusOutline
        List<DaysUnitRes> days = new ArrayList<>();
        DaysUnitRes day = new DaysUnitRes();
        day.setId(1L);
        day.setDayNumber(1);

        List<TrainingUnitRes> trainingUnits = new ArrayList<>();
        TrainingUnitRes trainingUnit = new TrainingUnitRes();
        trainingUnit.setId(1L);
        trainingUnit.setUnitNumber(1);
        trainingUnit.setUnitName("Unit 1");
        trainingUnit.setTrainingTime(1F);

        List<TrainingContentRes> trainingContents = new ArrayList<>();
        TrainingContentRes trainingContent = new TrainingContentRes();
        trainingContent.setId(1L);
        trainingContent.setOrderNumber(1);
        trainingContent.setContentName("Content 1");
        trainingContent.setDuration(60);
        trainingContent.setOutputStandards(List.of("H4SD"));
        trainingContent.setDeliveryType("Type 1");
        trainingContent.setMethod("Method 1");
        trainingContents.add(trainingContent);

        trainingUnit.setTrainingContents(trainingContents);
        trainingUnits.add(trainingUnit);

        day.setTrainingUnits(trainingUnits);
        days.add(day);

//        syllabusReqUpdate.setSyllabusOutline(new SyllabusOutlineRes(days));
        syllabusReqUpdate.setSyllabusOutline(new SyllabusOutlineRes(days));

        // Set syllabusOthers
        SyllabusOthersDTO syllabusOthers = new SyllabusOthersDTO();
        syllabusOthers.setAssessmentScheme(new AssessmentSchemeDTO(10, 20, 30, 40, 50, 60));
        syllabusOthers.setTrainingDeliveryPrinciple(new TrainingPrincipleDTO("Training", "Retest", "Marking", "Waiver Criteria", "Others"));
        syllabusReqUpdate.setSyllabusOthers(syllabusOthers);



        when(syllabusRepository.findByCode(syllabusReqUpdate.getCode())).thenReturn(java.util.Optional.of(testSyllabus));
        when(authenticationService.getName()).thenReturn("MockUser");
        when(learningObjectiveRepository.findByCode("")).thenReturn(Optional.empty()); // Default case
        when(learningObjectiveRepository.findByCode(testLearningObjective1.getCode())).thenReturn(Optional.of(testLearningObjective1));
        when(learningObjectiveRepository.findByCode(testLearningObjective2.getCode())).thenReturn(Optional.of(testLearningObjective2));
        when(daysUnitRepository.findById(1L)).thenReturn(Optional.of(testDayUnit));
        when(trainingUnitRepository.findById(1L)).thenReturn(Optional.of(testTrainingUnit));
        when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(testTrainingContent));
        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingContentRepository.save(any(TrainingContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(syllabusConverter.convertToDetailsRes(any(Syllabus.class))).thenAnswer(invocation -> {
            Syllabus newSyllabus = invocation.getArgument(0);
            return mapToDetailRes(newSyllabus);
        });


        // Act
        SyllabusDetailsRes updatedSyllabusAsDraft = syllabusService.updateSyllabusAsDraft(syllabusReqUpdate);
//        SyllabusDetailsRes updatedSyllabusAsActive = syllabusService.updateSyllabusAsActive(syllabusReqUpdate);


        // Assert
        assertNotNull(updatedSyllabusAsDraft);
        assertBasicPropertiesOfDetailRes(updatedSyllabusAsDraft, syllabusReqUpdate);
//        assertNotNull(updatedSyllabusAsActive);
//        assertBasicPropertiesOfDetailRes(updatedSyllabusAsActive, syllabusReqUpdate);

        //verify
        assertEquals(2, testSyllabus.getStatus(), "The status should be set to 2 for a draft syllabus update");
    }



    @Test
    public void testUpdateSyllabusAsActive_Success(){

        // Arrange
        // Set SyllabusReqUpdate
        SyllabusReqUpdate syllabusReqUpdate = new SyllabusReqUpdate();
        syllabusReqUpdate.setCode("SYL2024");
        syllabusReqUpdate.setSyllabusName("Advanced Java");
        syllabusReqUpdate.setVersion(2);

        // Set syllabusGeneral
        syllabusReqUpdate.setSyllabusGeneral(new SyllabusGeneralDTO("Intermediate", 20,
                "Technical requirements", "Course objectives"));

        // Set syllabusOutline
        List<DaysUnitRes> days = new ArrayList<>();
        DaysUnitRes day = new DaysUnitRes();
        day.setId(1L);
        day.setDayNumber(1);

        List<TrainingUnitRes> trainingUnits = new ArrayList<>();
        TrainingUnitRes trainingUnit = new TrainingUnitRes();
        trainingUnit.setId(1L);
        trainingUnit.setUnitNumber(1);
        trainingUnit.setUnitName("Unit 1");
        trainingUnit.setTrainingTime(1F);

        List<TrainingContentRes> trainingContents = new ArrayList<>();
        TrainingContentRes trainingContent = new TrainingContentRes();
        trainingContent.setId(1L);
        trainingContent.setOrderNumber(1);
        trainingContent.setContentName("Content 1");
        trainingContent.setDuration(60);
        trainingContent.setOutputStandards(List.of("H4SD"));
        trainingContent.setDeliveryType("Type 1");
        trainingContent.setMethod("Method 1");
        trainingContents.add(trainingContent);

        trainingUnit.setTrainingContents(trainingContents);
        trainingUnits.add(trainingUnit);

        day.setTrainingUnits(trainingUnits);
        days.add(day);

//        syllabusReqUpdate.setSyllabusOutline(new SyllabusOutlineRes(days));
        syllabusReqUpdate.setSyllabusOutline(new SyllabusOutlineRes(days));

        // Set syllabusOthers
        SyllabusOthersDTO syllabusOthers = new SyllabusOthersDTO();
        syllabusOthers.setAssessmentScheme(new AssessmentSchemeDTO(20, 50, 30, 50, 50, 60));
        syllabusOthers.setTrainingDeliveryPrinciple(new TrainingPrincipleDTO("Training", "Retest", "Marking", "Waiver Criteria", "Others"));
        syllabusReqUpdate.setSyllabusOthers(syllabusOthers);



        when(syllabusRepository.findByCode(syllabusReqUpdate.getCode())).thenReturn(java.util.Optional.of(testSyllabus));
        when(authenticationService.getName()).thenReturn("MockUser");
        when(learningObjectiveRepository.findByCode("")).thenReturn(Optional.empty()); // Default case
        when(learningObjectiveRepository.findByCode(testLearningObjective1.getCode())).thenReturn(Optional.of(testLearningObjective1));
        when(learningObjectiveRepository.findByCode(testLearningObjective2.getCode())).thenReturn(Optional.of(testLearningObjective2));
        when(daysUnitRepository.findById(1L)).thenReturn(Optional.of(testDayUnit));
        when(trainingUnitRepository.findById(1L)).thenReturn(Optional.of(testTrainingUnit));
        when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(testTrainingContent));
        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingContentRepository.save(any(TrainingContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(syllabusConverter.convertToDetailsRes(any(Syllabus.class))).thenAnswer(invocation -> {
            Syllabus newSyllabus = invocation.getArgument(0);
            return mapToDetailRes(newSyllabus);
        });


        // Act
//        SyllabusDetailsRes updatedSyllabusAsDraft = syllabusService.updateSyllabusAsDraft(syllabusReqUpdate);
        SyllabusDetailsRes updatedSyllabusAsActive = syllabusService.updateSyllabusAsActive(syllabusReqUpdate);


        // Assert
//        assertNotNull(updatedSyllabusAsDraft);
//        assertBasicPropertiesOfDetailRes(updatedSyllabusAsDraft, syllabusReqUpdate);
        assertNotNull(updatedSyllabusAsActive);
        assertBasicPropertiesOfDetailRes(updatedSyllabusAsActive, syllabusReqUpdate);

        //verify
        assertEquals(1, testSyllabus.getStatus(), "The status should be set to 1 for a active syllabus update");
    }

    @Test
    public void testUpdateSyllabusAsActive_ResourceNotFoundException() {
        // Arrange
        SyllabusReqUpdate syllabusReqUpdate = new SyllabusReqUpdate();
        syllabusReqUpdate.setCode("SYL2024");

        when(syllabusRepository.findByCode(syllabusReqUpdate.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            syllabusService.updateSyllabusAsActive(syllabusReqUpdate);
        });
    }

    @Test
    public void testUpdateSyllabusAsDraft_ResourceNotFoundException() {
        // Arrange
        SyllabusReqUpdate syllabusReqUpdate = new SyllabusReqUpdate();
        syllabusReqUpdate.setCode("SYL2024");

        when(syllabusRepository.findByCode(syllabusReqUpdate.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            syllabusService.updateSyllabusAsDraft(syllabusReqUpdate);
        });
    }

    @Test
    public void testGetActiveSyllabusList_withName() {
        // Setup
        Syllabus syllabus = new Syllabus();
        syllabus.setCode("SYL101");
        syllabus.setStatus(1);
        ActiveSyllabus activeSyllabus = new ActiveSyllabus();
        activeSyllabus.setCode("SYL101");
        activeSyllabus.setStatus(1);
        when(syllabusRepository.findAllByNameContainingIgnoreCaseAndStatus("test", 1))
                .thenReturn(List.of(syllabus));
        when(syllabusConverter.convertToActiveList(syllabus)).thenReturn(activeSyllabus);

        // Execute
        List<ActiveSyllabus> result = syllabusService.getActiveSyllabusList("test");

        // Verify
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        // You can add more detailed assertions here

        // Verify interactions
        verify(syllabusRepository).findAllByNameContainingIgnoreCaseAndStatus("test", 1);
        verify(syllabusConverter).convertToActiveList(syllabus);
    }

    @Test
    public void testGetActiveSyllabusList_withNullName() {
        // Setup
        Syllabus syllabus = new Syllabus();
        syllabus.setCode("SYL101");
        syllabus.setStatus(1);
        ActiveSyllabus activeSyllabus = new ActiveSyllabus();
        activeSyllabus.setCode("SYL101");
        activeSyllabus.setStatus(1);
        when(syllabusRepository.findAllByStatus(1))
                .thenReturn(List.of(syllabus));
        when(syllabusConverter.convertToActiveList(syllabus)).thenReturn(activeSyllabus);

        // Execute
        List<ActiveSyllabus> result = syllabusService.getActiveSyllabusList(null);

        // Verify
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        // You can add more detailed assertions here

        // Verify interactions
        verify(syllabusRepository).findAllByStatus(1);
        verify(syllabusConverter).convertToActiveList(syllabus);
    }

    @Test
    void testUpdateSyllabus_DeletionsAndAdditions() {
        // Mock SyllabusReqUpdate input
        SyllabusReqUpdate syllabusReqUpdate = new SyllabusReqUpdate();
        syllabusReqUpdate.setCode("SYL2024");
        syllabusReqUpdate.setSyllabusName("Advanced Java");
        syllabusReqUpdate.setVersion(2);
        syllabusReqUpdate.setDeletedDaysId(List.of(1L));
        syllabusReqUpdate.setDeletedTrainingUnitsId(List.of(2L));
        syllabusReqUpdate.setDeletedTrainingContentsId(List.of(3L));
        // Set syllabusGeneral
        syllabusReqUpdate.setSyllabusGeneral(new SyllabusGeneralDTO("Intermediate", 20,
                "Technical requirements", "Course objectives"));

        // Set syllabusOutline
        List<DaysUnitRes> days = new ArrayList<>();
        DaysUnitRes day = new DaysUnitRes();
        day.setId(1L);
        day.setDayNumber(1);

        List<TrainingUnitRes> trainingUnits = new ArrayList<>();
        TrainingUnitRes trainingUnit = new TrainingUnitRes();
        trainingUnit.setId(1L);
        trainingUnit.setUnitNumber(1);
        trainingUnit.setUnitName("Unit 1");
        trainingUnit.setTrainingTime(1F);

        List<TrainingContentRes> trainingContents = new ArrayList<>();
        TrainingContentRes trainingContent = new TrainingContentRes();
        trainingContent.setId(1L);
        trainingContent.setOrderNumber(1);
        trainingContent.setContentName("Content 1");
        trainingContent.setDuration(60);
        trainingContent.setOutputStandards(List.of("H4SD"));
        trainingContent.setDeliveryType("Type 1");
        trainingContent.setMethod("Method 1");
        trainingContents.add(trainingContent);

        trainingUnit.setTrainingContents(trainingContents);
        trainingUnits.add(trainingUnit);

        day.setTrainingUnits(trainingUnits);
        days.add(day);

//        syllabusReqUpdate.setSyllabusOutline(new SyllabusOutlineRes(days));
        syllabusReqUpdate.setSyllabusOutline(new SyllabusOutlineRes(days));

        // Set syllabusOthers
        SyllabusOthersDTO syllabusOthers = new SyllabusOthersDTO();
        syllabusOthers.setAssessmentScheme(new AssessmentSchemeDTO(20, 50, 30, 50, 50, 60));
        syllabusOthers.setTrainingDeliveryPrinciple(new TrainingPrincipleDTO("Training", "Retest", "Marking", "Waiver Criteria", "Others"));
        syllabusReqUpdate.setSyllabusOthers(syllabusOthers);
        // Initialize newSyllabus with necessary attributes...

        // Setup mock behavior for deletion
        when(daysUnitRepository.findById(1L)).thenReturn(Optional.of(new DaysUnit()));
        when(trainingUnitRepository.findById(2L)).thenReturn(Optional.of(new TrainingUnit()));
        when(trainingContentRepository.findById(3L)).thenReturn(Optional.of(new TrainingContent()));

        doNothing().when(daysUnitRepository).delete(any(DaysUnit.class));
        doNothing().when(trainingUnitRepository).delete(any(TrainingUnit.class));
        doNothing().when(trainingContentRepository).delete(any(TrainingContent.class));
        doNothing().when(learningMaterialRepository).deleteAll(anyList());

        // Mock behavior for adding new entities
        when(modelMapper.map(any(DaysUnitRes.class), eq(DaysUnitDTO.class))).thenReturn(new DaysUnitDTO());
        // Similar mockings for TrainingUnitDTO and TrainingContentDTO...
        when(daysUnitRepository.save(any(DaysUnit.class))).thenReturn(new DaysUnit()); // Assume this returns a populated entity

        // Execute the method under test
        when(syllabusRepository.findByCode(syllabusReqUpdate.getCode())).thenReturn(java.util.Optional.of(testSyllabus));
        when(authenticationService.getName()).thenReturn("MockUser");
        when(learningObjectiveRepository.findByCode("")).thenReturn(Optional.empty()); // Default case
        when(learningObjectiveRepository.findByCode(testLearningObjective1.getCode())).thenReturn(Optional.of(testLearningObjective1));
        when(learningObjectiveRepository.findByCode(testLearningObjective2.getCode())).thenReturn(Optional.of(testLearningObjective2));
        when(daysUnitRepository.findById(1L)).thenReturn(Optional.of(testDayUnit));
        when(trainingUnitRepository.findById(1L)).thenReturn(Optional.of(testTrainingUnit));
        when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(testTrainingContent));
        when(syllabusRepository.save(any(Syllabus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(daysUnitRepository.save(any(DaysUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingContentRepository.save(any(TrainingContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(syllabusConverter.convertToDetailsRes(any(Syllabus.class))).thenAnswer(invocation -> {
            Syllabus newSyllabus = invocation.getArgument(0);
            return mapToDetailRes(newSyllabus);
        });


        // Act
//        SyllabusDetailsRes updatedSyllabusAsDraft = syllabusService.updateSyllabusAsDraft(syllabusReqUpdate);
        SyllabusDetailsRes updatedSyllabusAsActive = syllabusService.updateSyllabusAsActive(syllabusReqUpdate);

        // Verify deletion interactions
        verify(daysUnitRepository, times(2)).findById(1L);
        verify(trainingUnitRepository, times(1)).findById(2L);
        verify(trainingContentRepository, times(1)).findById(3L);

        verify(daysUnitRepository, times(1)).delete(any(DaysUnit.class));
        verify(trainingUnitRepository, times(1)).delete(any(TrainingUnit.class));
        verify(trainingContentRepository, times(1)).delete(any(TrainingContent.class));
        verify(learningMaterialRepository, times(1)).deleteAll(anyList());

        // Add verifies for TrainingUnit and TrainingContent as well...
        verify(daysUnitRepository, times(1)).save(any(DaysUnit.class));
        // Similar verifies for saving TrainingUnit and TrainingContent
    }


    @Test
    public void testSearchSyllabusWithValidDates_Success() {
        String searchKey = "test";
        List<String> createdBy = Arrays.asList("user1", "user2");
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        Integer duration = 10;
        List<String> outputStandards = Arrays.asList("standard1", "standard2");
        List<Integer> statuses = Arrays.asList(1, 2);
        int page = 0;
        int size = 10;

        Page<Syllabus> mockedPage = new PageImpl<>(Arrays.asList(new Syllabus(), new Syllabus()));
        when(syllabusRepository.searchByDateNotNull(eq(searchKey), eq(createdBy), eq(startDate), eq(endDate), eq(duration), eq(outputStandards), eq(statuses), any()))
                .thenReturn(mockedPage);

        SyllabusPageRes mockPageRes = new SyllabusPageRes(); // Assuming SyllabusPageRes is a valid return type
        when(syllabusConverter.convertToPageRes(any())).thenReturn(mockPageRes);

        // Execute
        Page<SyllabusPageRes> resultPage = syllabusService.searchSyllabus(searchKey, createdBy, startDate, endDate, duration, outputStandards, statuses, page, size);

        // Verify
        verify(syllabusRepository, times(1)).searchByDateNotNull(eq(searchKey), eq(createdBy), eq(startDate), eq(endDate), eq(duration), eq(outputStandards), eq(statuses), any());
        verify(syllabusConverter, times(2)).convertToPageRes(any()); // Assuming each Syllabus in the page needs conversion

        // Assertions
        assertEquals(2, resultPage.getContent().size()); // Assuming the mockPage contains two Syllabus objects
    }

    @Test
    public void testSearchSyllabusWithNullDates_Success() {
        String searchKey = "test";
        List<String> createdBy = Arrays.asList("user1", "user2");
        Integer duration = 10;
        List<String> outputStandards = Arrays.asList("standard1", "standard2");
        List<Integer> statuses = Arrays.asList(1, 2);
        int page = 0;
        int size = 10;

        Page<Syllabus> mockedPage = new PageImpl<>(Arrays.asList(new Syllabus(), new Syllabus()));
        when(syllabusRepository.searchByDateNull(eq(searchKey), eq(createdBy), eq(duration), eq(outputStandards), eq(statuses), any()))
                .thenReturn(mockedPage);

        SyllabusPageRes mockPageRes = new SyllabusPageRes(); // Assuming SyllabusPageRes is a valid return type
        when(syllabusConverter.convertToPageRes(any())).thenReturn(mockPageRes);

        // Execute
        Page<SyllabusPageRes> resultPage = syllabusService.searchSyllabus(searchKey, createdBy, null, null, duration, outputStandards, statuses, page, size);

        // Verify
        verify(syllabusRepository, times(1)).searchByDateNull(eq(searchKey), eq(createdBy), eq(duration), eq(outputStandards), eq(statuses), any());
        verify(syllabusConverter, times(2)).convertToPageRes(any()); // Assuming each Syllabus in the page needs conversion

        // Assertions
        assertEquals(2, resultPage.getContent().size()); // Assuming the mockPage contains two Syllabus objects
    }

    @Test
    public void testSearchSyllabusEndDateNull_ThrowsException() {
        String searchKey = "test";
        List<String> createdBy = Arrays.asList("user1", "user2");
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        Integer duration = 10;
        List<String> outputStandards = Arrays.asList("standard1", "standard2");
        List<Integer> statuses = Arrays.asList(1, 2);
        int page = 0;
        int size = 10;

        // Attempt to execute
        ApiException exception = assertThrows(ApiException.class, () -> {
            syllabusService.searchSyllabus(searchKey, createdBy, startDate, null, duration, outputStandards, statuses, page, size);
        });

        // Verify that the correct message and HttpStatus are used in the exception
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("End Date must not be null or empty", exception.getMessage());
    }


    @Test
    public void testSearchSyllabusStartDateNull_ThrowsException() {
        String searchKey = "test";
        List<String> createdBy = Arrays.asList("user1", "user2");
        LocalDate endDate = LocalDate.of(2023, 1, 1);
        Integer duration = 10;
        List<String> outputStandards = Arrays.asList("standard1", "standard2");
        List<Integer> statuses = Arrays.asList(1, 2);
        int page = 0;
        int size = 10;

        // Attempt to execute
        ApiException exception = assertThrows(ApiException.class, () -> {
            syllabusService.searchSyllabus(searchKey, createdBy, null, endDate, duration, outputStandards, statuses, page, size);
        });

        // Verify that the correct message and HttpStatus are used in the exception
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals( "Start Date must not be null or empty", exception.getMessage());
    }

    void assertBasicPropertiesOfDetailRes(SyllabusDetailsRes result, SyllabusReqUpdate testSyllabusReq){
        // Properties within SyllabusGeneral part of SyllabusDetailsRes
        assertEquals(testSyllabusReq.getSyllabusGeneral().getLevel(), result.getSyllabusGeneral().getLevel(), "The levels should match.");
        assertEquals(testSyllabusReq.getSyllabusGeneral().getAttendeeNumber(), result.getSyllabusGeneral().getAttendeeNumber(), "The attendee numbers should match.");
        assertEquals(testSyllabusReq.getSyllabusGeneral().getTechnicalRequirements(), result.getSyllabusGeneral().getTechnicalRequirements(), "The technical requirements should match.");
        assertEquals(testSyllabusReq.getSyllabusGeneral().getCourseObjectives(), result.getSyllabusGeneral().getCourseObjectives(), "The course objectives should match.");

        // Assertions for assessment scheme within SyllabusOthers part of SyllabusDetailsRes
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getQuiz(), result.getSyllabusOthers().getAssessmentScheme().getQuiz(), "The quiz assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getAssignment(), result.getSyllabusOthers().getAssessmentScheme().getAssignment(), "The assignment assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().get_final(), result.getSyllabusOthers().getAssessmentScheme().get_final(), "The final assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getGpa(), result.getSyllabusOthers().getAssessmentScheme().getGpa(), "The GPA criteria values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalPractice(), result.getSyllabusOthers().getAssessmentScheme().getFinalPractice(), "The final practice assessment values should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalTheory(), result.getSyllabusOthers().getAssessmentScheme().getFinalTheory(), "The final theory assessment values should match.");

        // Assertions for training delivery principles within SyllabusOthers part of SyllabusDetailsRes
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getTraining(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getTraining(), "The training principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getRetest(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getRetest(), "The retest principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getMarking(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getMarking(), "The marking principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getWaiverCriteria(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getWaiverCriteria(), "The waiver criteria principles should match.");
        assertEquals(testSyllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getOthers(), result.getSyllabusOthers().getTrainingDeliveryPrinciple().getOthers(), "The others principles should match.");

        // Assert creator and modifier details
        assertEquals("MockUser", result.getModifiedBy(), "The creator should match the mocked user.");
        // For created and modified dates, you might want to check if they're not null since their exact value might be hard to predict
        assertNotNull(result.getModifiedDate(), "The created date should be set.");

        // Assert that the syllabus outline is correctly set
        assertNotNull(result.getSyllabusOutline(), "The syllabus outline should not be null.");

        // Assert the correct number of days units
        assertEquals(testSyllabusReq.getSyllabusOutline().getDays().size(), result.getSyllabusOutline().getDays().size(), "The number of days units should match.");

        // Asserting properties of DaysUnits, TrainingUnits, and TrainingContents
        for (DaysUnitRes expectedDayUnit : testSyllabusReq.getSyllabusOutline().getDays()) {
            DaysUnitRes actualDayUnit = result.getSyllabusOutline().getDays().stream()
                    .filter(actualDay -> actualDay.getDayNumber() == expectedDayUnit.getDayNumber())
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Expected day number not found: " + expectedDayUnit.getDayNumber()));

            // Now for each DaysUnit, assert the properties of TrainingUnits
            List<TrainingUnitRes> expectedTrainingUnits = expectedDayUnit.getTrainingUnits();
            List<TrainingUnitRes> actualTrainingUnits = actualDayUnit.getTrainingUnits();

            assertEquals(expectedTrainingUnits.size(), actualTrainingUnits.size(), "The number of training units should match for day " + expectedDayUnit.getDayNumber());

            for (TrainingUnitRes expectedTrainingUnit : expectedTrainingUnits) {
                TrainingUnitRes actualTrainingUnit = actualTrainingUnits.stream()
                        .filter(actualUnit -> actualUnit.getUnitNumber() == expectedTrainingUnit.getUnitNumber() && actualUnit.getUnitName().equals(expectedTrainingUnit.getUnitName()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Expected training unit not found: " + expectedTrainingUnit.getUnitName()));

                // Assert specific properties of TrainingUnit here, if any
                assertEquals(expectedTrainingUnit.getTrainingTime(), actualTrainingUnit.getTrainingTime(), "Duration should match for training unit: " + expectedTrainingUnit.getUnitName());

                // Now, for each TrainingUnit, assert the properties of TrainingContents
                List<TrainingContentRes> expectedTrainingContents = expectedTrainingUnit.getTrainingContents();
                List<TrainingContentRes> actualTrainingContents = actualTrainingUnit.getTrainingContents();

                assertEquals(expectedTrainingContents.size(), actualTrainingContents.size(), "The number of training contents should match for training unit " + expectedTrainingUnit.getUnitName());

                for (TrainingContentRes expectedTrainingContent : expectedTrainingContents) {
                    TrainingContentRes actualTrainingContent = actualTrainingContents.stream()
                            .filter(actualContent -> actualContent.getOrderNumber() == expectedTrainingContent.getOrderNumber() && actualContent.getContentName().equals(expectedTrainingContent.getContentName()))
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("Expected training content not found: " + expectedTrainingContent.getContentName()));

                    // Assert specific properties of TrainingContent here, such as duration, deliveryType, and method
                    assertEquals(expectedTrainingContent.getDuration(), actualTrainingContent.getDuration(), "Duration should match for content: " + expectedTrainingContent.getContentName());
                    assertEquals(expectedTrainingContent.getDeliveryType(), actualTrainingContent.getDeliveryType(), "Delivery type should match for content: " + expectedTrainingContent.getContentName());
                    assertEquals(expectedTrainingContent.getMethod(), actualTrainingContent.getMethod(), "Method should match for content: " + expectedTrainingContent.getContentName());
                    // If you need to assert the objective codes
                    assertEquals(expectedTrainingContent.getOutputStandards(), actualTrainingContent.getOutputStandards(), "Objective codes should match for content: " + expectedTrainingContent.getContentName());
                }
            }
        }
    }


}
