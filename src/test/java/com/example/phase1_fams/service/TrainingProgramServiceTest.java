package com.example.phase1_fams.service;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.TrainingProgramConverter;
import com.example.phase1_fams.dto.*;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.SyllabusReq;
import com.example.phase1_fams.dto.request.TrainingProgramReq;
import com.example.phase1_fams.dto.request.TrainingProgramReqUpdate;
import com.example.phase1_fams.dto.SyllabusGeneralDTO;
import com.example.phase1_fams.dto.response.TrainingProgramRes;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.SyllabusRepository;
import com.example.phase1_fams.repository.TrainingProgramRepository;
import com.example.phase1_fams.repository.TrainingProgramSyllabusRepository;
import com.example.phase1_fams.service.impl.TrainingProgramServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainingProgramServiceTest {

    @InjectMocks
    private TrainingProgramServiceImpl trainingProgramService;

    @Mock
    private TrainingProgramRepository trainingProgramRepository;

    @Mock
    private TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;

    @Mock
    private SyllabusRepository syllabusRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TrainingProgramConverter trainingProgramConverter;

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

    TrainingProgramSyllabusDTO testTrainingProgramSyllabusDTO;

    TrainingProgram testTrainingProgram;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init() {
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
                .trainingTime(60.0F)
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
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .status(1)
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
                .trainingTime(60.0F) // Assuming time is in minutes
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
        testTrainingProgramSyllabusDTO = TrainingProgramSyllabusDTO.builder()
                .sequence(1)
                .syllabusCode("SYL2024")
                .build();

        testTrainingProgram = TrainingProgram.builder()
                .id(1L)
                .name("Training Program 1")
                .description("updating")
                .duration(10)
                .status(1)
                .classes(new HashSet<>())
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();
    }

    @Test
    void testCreateTrainingProgramAsActive_Successfully() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                key,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);

        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
                    return dto;
                });
        when(syllabusRepository.findByCode(testTrainingProgramSyllabusDTO.getSyllabusCode()))
                .thenReturn(Optional.of(testSyllabus));
        when(authenticationService.getName())
                .thenReturn("admin");
        when(trainingProgramSyllabusRepository.save(any(TrainingProgramSyllabus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
                    trainingProgramRes.setId(1L);
                    trainingProgramRes.setTrainingProgramName("Training Program 1");
                    trainingProgramRes.setDuration(10);
                    trainingProgramRes.setStatus(1);
                    trainingProgramRes.setCreatedBy("admin");
                    trainingProgramRes.setGeneralInformation("updating");
                    trainingProgramRes.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    return trainingProgramRes;
                });

        TrainingProgramRes trainingProgramRes = trainingProgramService.createTrainingProgramAsActive(trainingProgramReq);

        assertNotNull(trainingProgramRes);
        assertEquals(trainingProgramReq.getName(), trainingProgramRes.getTrainingProgramName());
        assertEquals(trainingProgramReq.getDuration(), trainingProgramRes.getDuration());
        assertNotNull(trainingProgramRes.getCreatedBy());
        assertNotNull(trainingProgramRes.getCreatedDate());
        assertEquals(trainingProgramReq.getDuration(), trainingProgramRes.getDuration());
        assertEquals(trainingProgramReq.getDescription(), trainingProgramRes.getGeneralInformation());


        verify(trainingProgramRepository, times(1))
                .save(any(TrainingProgram.class));
        verify(syllabusRepository, times(1))
                .findByCode(anyString());
        verify(authenticationService, times(1))
                .getName();
        verify(trainingProgramSyllabusRepository, times(1))
                .save(any(TrainingProgramSyllabus.class));
        verify(trainingProgramConverter, times(1))
                .convertToTrainingProgramRes(any(TrainingProgram.class));

    }

    @Test
    void testCreateTrainingProgramAsActive_SyllabusListIsEmpty() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.createTrainingProgramAsActive(trainingProgramReq);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Syllabus list is empty!!!", exception.getMessage());

    }

    @Test
    void testCreateTrainingProgramAsActive_SyllabusIsNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setStatus(1);
                    return dto;
                });
        when(syllabusRepository.findByCode(anyString()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.createTrainingProgramAsActive(trainingProgramReq);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Syllabus not found to create", exception.getMessage());

    }

    @Test
    void testCreateTrainingProgramAsActive_SyllabusIsNotActive()  {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        TrainingProgramSyllabusKey keyPRN211 = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                keyPRN211,
                testSyllabus,
                testTrainingProgram,
                1
        );
        testSyllabus.setStatus(2);

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);

        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setCreatedBy("admin");
                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    dto.setStatus(1);
                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
                    return dto;
                });
        when(syllabusRepository.findByCode(testTrainingProgramSyllabusDTO.getSyllabusCode()))
                .thenReturn(Optional.of(testSyllabus));

        when(authenticationService.getName())
                .thenReturn("admin");

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.createTrainingProgramAsActive(trainingProgramReq);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Only syllabuses with Active status can used to create", exception.getMessage());

    }

    @Test
    void testCreateTrainingProgramAsDraft_Successfully() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        TrainingProgramSyllabusKey keyPRN211 = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                keyPRN211,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);

        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setCreatedBy("admin");
                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    dto.setStatus(1);
                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
                    return dto;
                });
        when(syllabusRepository.findByCode(testTrainingProgramSyllabusDTO.getSyllabusCode()))
                .thenReturn(Optional.of(testSyllabus));
        when(authenticationService.getName())
                .thenReturn("admin");
        when(trainingProgramSyllabusRepository.save(any(TrainingProgramSyllabus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
                    trainingProgramRes.setId(1L);
                    trainingProgramRes.setTrainingProgramName("Training Program 1");
                    trainingProgramRes.setDuration(10);
                    trainingProgramRes.setStatus(1);
                    trainingProgramRes.setCreatedBy("admin");
                    trainingProgramRes.setGeneralInformation("updating");
                    trainingProgramRes.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    return trainingProgramRes;
                });

        TrainingProgramRes trainingProgramRes = trainingProgramService.createTrainingProgramAsDraft(trainingProgramReq);

        assertNotNull(trainingProgramRes);
        assertEquals(trainingProgramReq.getName(), trainingProgramRes.getTrainingProgramName());
        assertEquals(trainingProgramReq.getDuration(), trainingProgramRes.getDuration());
        assertNotNull(trainingProgramRes.getCreatedBy());
        assertNotNull(trainingProgramRes.getCreatedDate());
        assertEquals(trainingProgramReq.getDuration(), trainingProgramRes.getDuration());
        assertEquals(trainingProgramReq.getDescription(), trainingProgramRes.getGeneralInformation());


        verify(trainingProgramRepository, times(1))
                .save(any(TrainingProgram.class));
        verify(syllabusRepository, times(1))
                .findByCode(anyString());
        verify(authenticationService, times(1))
                .getName();
        verify(trainingProgramSyllabusRepository, times(1))
                .save(any(TrainingProgramSyllabus.class));
        verify(trainingProgramConverter, times(1))
                .convertToTrainingProgramRes(any(TrainingProgram.class));

    }

    @Test
    void testCreateTrainingProgramAsDraft_SyllabusIsNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setStatus(1);
                    return dto;
                });
        when(syllabusRepository.findByCode(anyString()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.createTrainingProgramAsDraft(trainingProgramReq);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Syllabus not found to create", exception.getMessage());

    }

    @Test
    void testCreateTrainingProgramAsDraft_SyllabusIsNotActive()  {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                key,
                testSyllabus,
                testTrainingProgram,
                1
        );
        testSyllabus.setStatus(2);
        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);

        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setStatus(1);
                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
                    return dto;
                });
        when(syllabusRepository.findByCode(testTrainingProgramSyllabusDTO.getSyllabusCode()))
                .thenReturn(Optional.of(testSyllabus));
        when(authenticationService.getName())
                .thenReturn("admin");

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.createTrainingProgramAsDraft(trainingProgramReq);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Only syllabuses with Active status can used to create", exception.getMessage());

    }

    @Test
    void testUpdateTrainingProgramAsActive_Successfully() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                key,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        Set<TrainingProgramSyllabus> testSet = new HashSet<>();

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.of(testTrainingProgram));
        when(trainingProgramSyllabusRepository.findByTrainingProgramId(trainingProgramReqUpdate.getId()))
                .thenReturn(testSet);
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setModifiedBy("admin");
                    dto.setModifiedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    dto.setStatus(1);
                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
                    return dto;
                });
        when(syllabusRepository.findByCode(testTrainingProgramSyllabusDTO.getSyllabusCode()))
                .thenReturn(Optional.of(testSyllabus));
        when(trainingProgramSyllabusRepository.save(any(TrainingProgramSyllabus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
                    trainingProgramRes.setId(1L);
                    trainingProgramRes.setTrainingProgramName("Training Program 1");
                    trainingProgramRes.setDuration(10);
                    trainingProgramRes.setStatus(1);
                    trainingProgramRes.setModifiedBy("admin");
                    trainingProgramRes.setGeneralInformation("updating");
                    trainingProgramRes.setModifiedDate(LocalDate.of(2024, Calendar.FEBRUARY, 28));
                    return trainingProgramRes;
                });

        TrainingProgramRes trainingProgramRes = trainingProgramService.updateTrainingProgramAsActive(trainingProgramReqUpdate);

        assertNotNull(trainingProgramRes);
        assertEquals(trainingProgramReqUpdate.getId(), trainingProgramRes.getId());
        assertEquals(trainingProgramReqUpdate.getName(), trainingProgramRes.getTrainingProgramName());
        assertEquals(trainingProgramReqUpdate.getDuration(), trainingProgramRes.getDuration());
        assertEquals(trainingProgramReqUpdate.getDuration(), trainingProgramRes.getDuration());
        assertEquals(trainingProgramReqUpdate.getDescription(), trainingProgramRes.getGeneralInformation());

        verify(trainingProgramRepository, times(1))
                .findById(trainingProgramReqUpdate.getId());
        verify(trainingProgramRepository, times(1))
                .save(any(TrainingProgram.class));
        verify(syllabusRepository, times(1))
                .findByCode(anyString());
        verify(trainingProgramSyllabusRepository, times(1))
                .save(any(TrainingProgramSyllabus.class));
        verify(trainingProgramConverter, times(1))
                .convertToTrainingProgramRes(any(TrainingProgram.class));
        verify(trainingProgramSyllabusRepository, times(1))
                .findByTrainingProgramId(trainingProgramReqUpdate.getId());
    }



    @Test
    void testUpdateTrainingProgramAsActive_TrainingProgramIsNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.updateTrainingProgramAsActive(trainingProgramReqUpdate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("This training program is not found to update", exception.getMessage());

    }

    @Test
    void testUpdateTrainingProgramAsActive_SyllabusIsNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );
        Set<TrainingProgramSyllabus> testSet = new HashSet<>();

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.of(testTrainingProgram));
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setModifiedBy("admin");
                    dto.setModifiedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    dto.setStatus(2);
                    return dto;
                });
        when(trainingProgramSyllabusRepository.findByTrainingProgramId(trainingProgramReqUpdate.getId()))
                .thenReturn(testSet);
        when(syllabusRepository.findByCode(anyString()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.updateTrainingProgramAsActive(trainingProgramReqUpdate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Syllabus not found to create", exception.getMessage());

    }

    @Test
    void testUpdateTrainingProgramAsDraft_Successfully() {
        testTrainingProgram.setStatus(2);
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                key,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        Set<TrainingProgramSyllabus> testSet = new HashSet<>();

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.of(testTrainingProgram));
        when(trainingProgramSyllabusRepository.findByTrainingProgramId(trainingProgramReqUpdate.getId()))
                .thenReturn(testSet);
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setCreatedBy("admin");
                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    dto.setStatus(1);
                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
                    return dto;
                });
        when(syllabusRepository.findByCode(testTrainingProgramSyllabusDTO.getSyllabusCode()))
                .thenReturn(Optional.of(testSyllabus));
        when(trainingProgramSyllabusRepository.save(any(TrainingProgramSyllabus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
                    trainingProgramRes.setId(1L);
                    trainingProgramRes.setTrainingProgramName("Training Program 1");
                    trainingProgramRes.setDuration(10);
                    trainingProgramRes.setStatus(1);
                    trainingProgramRes.setModifiedBy("admin");
                    trainingProgramRes.setGeneralInformation("updating");
                    trainingProgramRes.setModifiedDate(LocalDate.of(2024, Calendar.FEBRUARY, 28));
                    return trainingProgramRes;
                });

        TrainingProgramRes trainingProgramRes = trainingProgramService.updateTrainingProgramAsDraft(trainingProgramReqUpdate);

        assertNotNull(trainingProgramRes);
        assertEquals(trainingProgramReqUpdate.getId(), trainingProgramRes.getId());
        assertEquals(trainingProgramReqUpdate.getName(), trainingProgramRes.getTrainingProgramName());
        assertEquals(trainingProgramReqUpdate.getDuration(), trainingProgramRes.getDuration());
        assertEquals(trainingProgramReqUpdate.getDuration(), trainingProgramRes.getDuration());
        assertEquals(trainingProgramReqUpdate.getDescription(), trainingProgramRes.getGeneralInformation());

        verify(trainingProgramRepository, times(1))
                .findById(trainingProgramReqUpdate.getId());
        verify(trainingProgramRepository, times(1))
                .save(any(TrainingProgram.class));
        verify(syllabusRepository, times(1))
                .findByCode(anyString());
        verify(trainingProgramSyllabusRepository, times(1))
                .save(any(TrainingProgramSyllabus.class));
        verify(trainingProgramConverter, times(1))
                .convertToTrainingProgramRes(any(TrainingProgram.class));
        verify(trainingProgramSyllabusRepository, times(1))
                .findByTrainingProgramId(trainingProgramReqUpdate.getId());
    }



    @Test
    void testUpdateTrainingProgramAsDraft_TrainingProgramIsNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.updateTrainingProgramAsDraft(trainingProgramReqUpdate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("This training program is not found to update", exception.getMessage());

    }

    @Test
    void testUpdateTrainingProgramAsDraft_SyllabusIsNotFound() {
        testTrainingProgram.setStatus(2);
        testTrainingProgram.setClasses(new HashSet<>());
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(testTrainingProgramSyllabusDTO);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                10,
                "updating",
                trainingProgramSyllabusDTOSet
        );
        Set<TrainingProgramSyllabus> testSet = new HashSet<>();

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.of(testTrainingProgram));
        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setModifiedBy("admin");
                    dto.setModifiedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    dto.setStatus(2);
                    return dto;
                });
        when(trainingProgramSyllabusRepository.findByTrainingProgramId(trainingProgramReqUpdate.getId()))
                .thenReturn(testSet);
        when(syllabusRepository.findByCode(anyString()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramService.updateTrainingProgramAsDraft(trainingProgramReqUpdate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Syllabus not found to create", exception.getMessage());

    }
}