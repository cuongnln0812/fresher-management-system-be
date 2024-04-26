package com.example.phase1_fams.service;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.TrainingProgramConverter;
import com.example.phase1_fams.dto.LearningMaterialDto;
import com.example.phase1_fams.dto.TrainingProgramDTO;
import com.example.phase1_fams.dto.TrainingProgramSyllabusDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.TrainingProgramReq;
import com.example.phase1_fams.dto.request.TrainingProgramReqUpdate;
import com.example.phase1_fams.dto.response.*;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.SyllabusRepository;
import com.example.phase1_fams.repository.TrainingProgramRepository;
import com.example.phase1_fams.repository.TrainingProgramSyllabusRepository;
import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.service.impl.TrainingProgramServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class TrainingProgramServiceTests {
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private LearningMaterialService learningMaterialService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private SyllabusRepository syllabusRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;
    @Mock
    private TrainingProgramConverter trainingProgramConverter;
    @InjectMocks
    private TrainingProgramServiceImpl trainingProgramService;

    TrainingProgram testExistingTrainingProgram;
    Users testUser;
    Role role;
    Syllabus testSyllabus;
    TrainingProgramSyllabus testTrainingProgramSyllabus;
    DaysUnit testDayUnit;
    TrainingUnit testTrainingUnit;
    TrainingContent testTrainingContent;
    LearningObjective testLearningObjective1;
    LearningObjective testLearningObjective2;
    Set<DaysUnit> daysUnitSet = new HashSet<>();
    Set<TrainingUnit> trainingUnitSet = new HashSet<>();
    Set<TrainingContent> trainingContentSet = new HashSet<>();
    Set<LearningObjective> learningObjectiveSet = new HashSet<>();
    Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();

    TrainingProgramSyllabusDTO trainingProgramSyllabusDTOPRN211;
    TrainingProgramSyllabusDTO trainingProgramSyllabusDTOPRO192;
    Syllabus syllabusPRN211;
    Syllabus syllabusPRO192;
    TrainingProgram trainingProgramAsActive;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    void init(){
        role = Role.builder()
                .roleId(1)
                .build();

        testUser = Users.builder()
                .id(1L)
                .name("MockUser")
                .email("mockuser@gmail.com")
                .role(role)
                .build();

        testExistingTrainingProgram = TrainingProgram.builder()
                .id(1L)
                .name("Dev Ops Foundation")
                .description("Developing Operator for beginner/fresher")
                .duration(7) //days
                .status(1)
                .trainingProgramSyllabusSet(trainingProgramSyllabusSet)
                .createdBy("MockUser")
                .createdDate(LocalDate.of(2024, 1, 23))
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

        trainingProgramSyllabusDTOPRN211 = TrainingProgramSyllabusDTO.builder()
                .sequence(1)
                .syllabusCode("PRN211")
                .build();
        trainingProgramSyllabusDTOPRO192 = TrainingProgramSyllabusDTO.builder()
                .sequence(2)
                .syllabusCode("PRO192")
                .build();
        trainingProgramAsActive = TrainingProgram.builder()
                .name("Training Program 1")
                .id(1L)
                .status(1)
                .createdBy("admin")
                .createdDate(LocalDate.of(2024, Calendar.FEBRUARY, 23))
                .description("updating")
                .build();
        syllabusPRN211 = Syllabus.builder()
                .code("PRN211")
                .name("Basic programming with C#")
                .build();
        syllabusPRO192 = Syllabus.builder()
                .code("PRO192")
                .name("OOP with Java")
                .build();

        testTrainingProgramSyllabus = new TrainingProgramSyllabus();
        testTrainingProgramSyllabus.setId(new TrainingProgramSyllabusKey(testSyllabus.getCode(), testExistingTrainingProgram.getId()));
        testTrainingProgramSyllabus.setTrainingProgram(testExistingTrainingProgram);
        testTrainingProgramSyllabus.setSyllabus(testSyllabus);
        testTrainingProgramSyllabus.setSequence(1);

        trainingProgramSyllabusSet.add(testTrainingProgramSyllabus);
        daysUnitSet.add(testDayUnit);
        trainingUnitSet.add(testTrainingUnit);
        trainingContentSet.add(testTrainingContent);
        learningObjectiveSet.add(testLearningObjective1);
        learningObjectiveSet.add(testLearningObjective2);
    }

    @Test
    void testGetTrainingProgramDetails_Success(){
        Long id = 1L;
        //Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        when(trainingProgramRepository.findById(id)).thenReturn(Optional.of(testExistingTrainingProgram));
        when(usersRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(trainingProgramConverter.convertToTrainingProgramRes(testExistingTrainingProgram)).thenAnswer(invocation -> {
            TrainingProgram trainingProgram = invocation.getArgument(0);
            return mapToDetailRes(trainingProgram);
        });

        TrainingProgramRes result = trainingProgramService.getTrainingProgramDetails(id);

        // Compare actual and expected objects
        assertEqualsTrainingProgramProperties(result, testExistingTrainingProgram);
    }

    @Test
    void testGetTrainingProgramDetails_TrainingProgramNotFound(){
        Long id = 2L;
        //Arrange
        when(trainingProgramRepository.findById(id)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> trainingProgramService.getTrainingProgramDetails(id),
                "Expected ApiException to be thrown for not found TrainingProgram");

        assertTrue(exception.getMessage().contains("Training Program not found!"));
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetTrainingProgramDetails_Unauthorized(){
        Long id = 2L;
        Role trainerRole = Role.builder()
                .roleId(3)
                .build();
        Users trainer = Users.builder()
                .email("trainer@gmail.com")
                .role(trainerRole)
                .build();
        TrainingProgram otherStatus = TrainingProgram.builder()
                .status(2)
                .build();

        //Arrange
        when(trainingProgramRepository.findById(id)).thenReturn(Optional.of(otherStatus));
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(trainer.getEmail());
        when(usersRepository.findByEmail(trainer.getEmail())).thenReturn(Optional.of(trainer));

        ApiException exception = assertThrows(ApiException.class,
                () -> trainingProgramService.getTrainingProgramDetails(id),
                "Expected ApiException to be thrown for unauthorized user accessing Training Programs with other status");

        assertTrue(exception.getMessage().contains("You dont have the authority to access to this Training Program!"));
        assertEquals(exception.getStatus(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetTrainingProgramDetails_NoUserLoggedIn(){
        Long id = 1L;

        //Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(String.valueOf(Optional.empty()));
        when(trainingProgramRepository.findById(id)).thenReturn(Optional.of(testExistingTrainingProgram));
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> trainingProgramService.getTrainingProgramDetails(id),
                "Expected ApiException to be thrown for unauthorized user accessing Training Programs with other status");

        assertTrue(exception.getMessage().contains( "Cannot find who is logged in"));
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }


//    @Test
//    void shouldSuccessfullyCreateTrainingProgramAsActive() {
//        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
//        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
//                "Training Program 1",
//                0,
//                "updating",
//                trainingProgramSyllabusDTOSet
//        );
//
//        TrainingProgramSyllabusKey keyPRN211 = new TrainingProgramSyllabusKey(syllabusPRN211.getCode(), trainingProgramAsActive.getId());
//        TrainingProgramSyllabus trainingProgramSyllabusPRN211 = new TrainingProgramSyllabus(
//                keyPRN211,
//                syllabusPRN211,
//                trainingProgramAsActive,
//                1
//        );
//        TrainingProgramSyllabusKey keyPRO192 = new TrainingProgramSyllabusKey(syllabusPRO192.getCode(), trainingProgramAsActive.getId());
//        TrainingProgramSyllabus trainingProgramSyllabusPRO192 = new TrainingProgramSyllabus(
//                keyPRO192,
//                syllabusPRO192,
//                trainingProgramAsActive,
//                2
//        );
//
//        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
//        trainingProgramSyllabusSet.add(trainingProgramSyllabusPRN211);
//        trainingProgramSyllabusSet.add(trainingProgramSyllabusPRO192);
//        trainingProgramAsActive.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
//
//        DaysUnit daysUnitPRN211 = new DaysUnit();
//        daysUnitPRN211.setId(1L);
//        daysUnitPRN211.setSyllabus(syllabusPRN211);
//        daysUnitPRN211.setDayNumber(10);;
//        Set<DaysUnit> daysUnitsPRN211 = new HashSet<>();
//        daysUnitsPRN211.add(daysUnitPRN211);
//        syllabusPRN211.setDaysUnits(daysUnitsPRN211);
//
//        DaysUnit daysUnitPRO192 = new DaysUnit();
//        daysUnitPRN211.setId(1L);
//        daysUnitPRN211.setSyllabus(syllabusPRO192);
//        daysUnitPRN211.setDayNumber(10);;
//        Set<DaysUnit> daysUnitsPRO192 = new HashSet<>();
//        daysUnitsPRN211.add(daysUnitPRO192);
//        syllabusPRO192.setDaysUnits(daysUnitsPRO192);
//
//        when(trainingProgramRepository.save(any(TrainingProgram.class)))
//                .thenAnswer(invocationOnMock -> {
//                    TrainingProgram dto = new TrainingProgram();
//                    dto.setId(1L);
//                    dto.setName("Training Program 1");
//                    dto.setDescription("updating");
//                    dto.setCreatedBy("admin");
//                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
//                    dto.setStatus(1);
//                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
//                    return dto;
//                });
//        when(authenticationService.getName()).thenReturn("MockUser");
//        when(syllabusRepository.findByCode(trainingProgramSyllabusDTOPRN211.getSyllabusCode()))
//                .thenReturn(Optional.of(syllabusPRN211));
//        when(syllabusRepository.findByCode(trainingProgramSyllabusDTOPRO192.getSyllabusCode()))
//                .thenReturn(Optional.of(syllabusPRO192));
//        when(syllabusRepository.getSyllabusDuration(syllabusPRN211.getCode()))
//                .thenReturn(0);
//        when(syllabusRepository.getSyllabusDuration(syllabusPRO192.getCode()))
//                .thenReturn(0);
//        when(trainingProgramSyllabusRepository.save(any(TrainingProgramSyllabus.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        TrainingProgramRes trainingProgramRes = trainingProgramService.createTrainingProgramAsActive(trainingProgramReq);
//
//        assertNotNull(trainingProgramRes);
//        assertEquals(trainingProgramReq.getName(), trainingProgramRes.getTrainingProgramName());
//        assertEquals(trainingProgramReq.getDuration(), trainingProgramRes.getDuration());
//        assertEquals("MockUser", trainingProgramRes.getCreatedBy());
//        assertNotNull(trainingProgramRes.getCreatedDate());
//        assertEquals(trainingProgramReq.getDescription(), trainingProgramRes.getGeneralInformation());
//        assertEquals(trainingProgramReq.getTrainingProgramDTOSet().size(), trainingProgramRes.getSyllabusContents().size());
//
//        verify(trainingProgramRepository, times(1))
//                .save(any(TrainingProgram.class));
//        verify(syllabusRepository, times(2))
//                .findByCode(anyString());
//        verify(trainingProgramSyllabusRepository, times(2))
//                .save(any(TrainingProgramSyllabus.class));
//        verify(syllabusRepository, times(2))
//                .getSyllabusDuration(any());
//        verify(trainingProgramSyllabusRepository, times(2))
//                .save(any(TrainingProgramSyllabus.class));
//
//    }
//
//    @Test
//    void shouldThrowApiExceptionCreateTrainingProgramAsActiveWhenSyllabusNotFound() {
//        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
//        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
//                "Training Program 1",
//                3,
//                "updating",
//                trainingProgramSyllabusDTOSet
//        );
//
//        when(trainingProgramRepository.save(any(TrainingProgram.class)))
//                .thenAnswer(invocationOnMock -> {
//                    TrainingProgram dto = new TrainingProgram();
//                    dto.setId(1L);
//                    dto.setName("Training Program 1");
//                    dto.setDescription("updating");
//                    dto.setCreatedBy("admin");
//                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
//                    dto.setStatus(1);
//                    return dto;
//                });
//        when(syllabusRepository.findByCode(anyString()))
//                .thenReturn(Optional.empty());
//
//        ApiException exception = assertThrows(ApiException.class, () -> {
//            trainingProgramService.createTrainingProgramAsActive(trainingProgramReq);
//        });
//
//        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
//        assertEquals("Syllabus not found to create", exception.getMessage());
//
//    }
//
//    @Test
//    void shouldThrowApiExceptionCreateTrainingProgramAsActiveWhenTrainingProgramConvertToTrainingProgramResFail() {
//        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
//        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
//                "Training Program 1",
//                3,
//                "updating",
//                trainingProgramSyllabusDTOSet
//        );
//
//        TrainingProgramSyllabusKey keyPRN211 = new TrainingProgramSyllabusKey(syllabusPRN211.getCode(), trainingProgramAsActive.getId());
//        TrainingProgramSyllabus trainingProgramSyllabusPRN211 = new TrainingProgramSyllabus(
//                keyPRN211,
//                syllabusPRN211,
//                trainingProgramAsActive,
//                1
//        );
//        TrainingProgramSyllabusKey keyPRO192 = new TrainingProgramSyllabusKey(syllabusPRO192.getCode(), trainingProgramAsActive.getId());
//        TrainingProgramSyllabus trainingProgramSyllabusPRO192 = new TrainingProgramSyllabus(
//                keyPRO192,
//                syllabusPRO192,
//                trainingProgramAsActive,
//                2
//        );
//
//        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
//        trainingProgramSyllabusSet.add(trainingProgramSyllabusPRN211);
//        trainingProgramSyllabusSet.add(trainingProgramSyllabusPRO192);
//        trainingProgramAsActive.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
//
//        when(trainingProgramRepository.save(any(TrainingProgram.class)))
//                .thenAnswer(invocationOnMock -> {
//                    TrainingProgram dto = new TrainingProgram();
//                    dto.setId(1L);
//                    dto.setName("Training Program 1");
//                    dto.setDescription("updating");
//                    dto.setCreatedBy("admin");
//                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
//                    dto.setStatus(1);
//                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
//                    return dto;
//                });
//        when(syllabusRepository.findByCode(trainingProgramSyllabusDTOPRN211.getSyllabusCode()))
//                .thenReturn(Optional.of(syllabusPRN211));
//        when(syllabusRepository.findByCode(trainingProgramSyllabusDTOPRO192.getSyllabusCode()))
//                .thenReturn(Optional.of(syllabusPRO192));
//
//        ApiException exception = assertThrows(ApiException.class, () -> {
//            trainingProgramService.createTrainingProgramAsActive(trainingProgramReq);
//        });
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
//        assertEquals("Error mapping training program details!", exception.getMessage());
//
//    }
//
//    @Test
//    void shouldSuccessfullyCreateTrainingProgramAsDraft() {
//        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
//        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
//        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
//                "Training Program 1",
//                3,
//                "updating",
//                trainingProgramSyllabusDTOSet
//        );
//
//        TrainingProgramSyllabusKey keyPRN211 = new TrainingProgramSyllabusKey(syllabusPRN211.getCode(), trainingProgramAsActive.getId());
//        TrainingProgramSyllabus trainingProgramSyllabusPRN211 = new TrainingProgramSyllabus(
//                keyPRN211,
//                syllabusPRN211,
//                trainingProgramAsActive,
//                1
//        );
//        TrainingProgramSyllabusKey keyPRO192 = new TrainingProgramSyllabusKey(syllabusPRO192.getCode(), trainingProgramAsActive.getId());
//        TrainingProgramSyllabus trainingProgramSyllabusPRO192 = new TrainingProgramSyllabus(
//                keyPRO192,
//                syllabusPRO192,
//                trainingProgramAsActive,
//                2
//        );
//
//        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
//        trainingProgramSyllabusSet.add(trainingProgramSyllabusPRN211);
//        trainingProgramSyllabusSet.add(trainingProgramSyllabusPRO192);
//        trainingProgramAsActive.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
//
//        DaysUnit daysUnitPRN211 = new DaysUnit();
//        daysUnitPRN211.setId(1L);
//        daysUnitPRN211.setSyllabus(syllabusPRN211);
//        daysUnitPRN211.setDayNumber(10);;
//        Set<DaysUnit> daysUnitsPRN211 = new HashSet<>();
//        daysUnitsPRN211.add(daysUnitPRN211);
//        syllabusPRN211.setDaysUnits(daysUnitsPRN211);
//
//        DaysUnit daysUnitPRO192 = new DaysUnit();
//        daysUnitPRN211.setId(1L);
//        daysUnitPRN211.setSyllabus(syllabusPRO192);
//        daysUnitPRN211.setDayNumber(10);;
//        Set<DaysUnit> daysUnitsPRO192 = new HashSet<>();
//        daysUnitsPRN211.add(daysUnitPRO192);
//        syllabusPRO192.setDaysUnits(daysUnitsPRO192);
//
//        when(authenticationService.getName()).thenReturn("MockUser");
//        when(trainingProgramRepository.save(any(TrainingProgram.class)))
//                .thenAnswer(invocationOnMock -> {
//                    TrainingProgram dto = new TrainingProgram();
//                    dto.setId(1L);
//                    dto.setName("Training Program 1");
//                    dto.setDescription("updating");
//                    dto.setCreatedBy("admin");
//                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
//                    dto.setStatus(2);
//                    dto.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
//                    return dto;
//                });
//        when(syllabusRepository.findByCode(trainingProgramSyllabusDTOPRN211.getSyllabusCode()))
//                .thenReturn(Optional.of(syllabusPRN211));
//        when(syllabusRepository.findByCode(trainingProgramSyllabusDTOPRO192.getSyllabusCode()))
//                .thenReturn(Optional.of(syllabusPRO192));
//        when(syllabusRepository.getSyllabusDuration(syllabusPRN211.getCode()))
//                .thenReturn(0);
//        when(syllabusRepository.getSyllabusDuration(syllabusPRO192.getCode()))
//                .thenReturn(0);
//        when(trainingProgramSyllabusRepository.save(any(TrainingProgramSyllabus.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        TrainingProgramRes trainingProgramRes = trainingProgramService.createTrainingProgramAsDraft(trainingProgramReq);
//
//        assertNotNull(trainingProgramRes);
//        assertEquals(trainingProgramReq.getName(), trainingProgramRes.getTrainingProgramName());
//        assertEquals(trainingProgramReq.getDuration(), trainingProgramRes.getDuration());
//        assertEquals("MockUser", trainingProgramRes.getCreatedBy());
//        assertNotNull(trainingProgramRes.getCreatedDate());
//        assertEquals(trainingProgramReq.getDescription(), trainingProgramRes.getGeneralInformation());
//        assertEquals(trainingProgramReq.getTrainingProgramDTOSet().size(), trainingProgramRes.getSyllabusContents().size());
//
//        verify(trainingProgramRepository, times(1))
//                .save(any(TrainingProgram.class));
//        verify(syllabusRepository, times(2))
//                .findByCode(anyString());
//        verify(trainingProgramSyllabusRepository, times(2))
//                .save(any(TrainingProgramSyllabus.class));
//        verify(syllabusRepository, times(2))
//                .getSyllabusDuration(anyString());
//        verify(trainingProgramSyllabusRepository, times(2))
//                .save(any(TrainingProgramSyllabus.class));
//
//    }

    @Test
    void shouldThrowApiExceptionCreateTrainingProgramAsDraftWhenSyllabusNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
        TrainingProgramReq trainingProgramReq = new TrainingProgramReq(
                "Training Program 1",
                3,
                "updating",
                trainingProgramSyllabusDTOSet
        );

        when(trainingProgramRepository.save(any(TrainingProgram.class)))
                .thenAnswer(invocationOnMock -> {
                    TrainingProgram dto = new TrainingProgram();
                    dto.setId(1L);
                    dto.setName("Training Program 1");
                    dto.setDescription("updating");
                    dto.setCreatedBy("admin");
                    dto.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    dto.setStatus(2);
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
    void shouldThrowApiExceptionUpdateTrainingProgramAsActiveWhenTrainingProgramNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                0,
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
    void shouldThrowApiExceptionUpdateTrainingProgramAsActiveWhenSyllabusNotFound() {
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                0,
                "updating",
                trainingProgramSyllabusDTOSet
        );
        Set<TrainingProgramSyllabus> testSet = new HashSet<>();

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.of(trainingProgramAsActive));
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
    void shouldThrowApiExceptionUpdateTrainingProgramAsDraftWhenTrainingProgramNotFound() {
        trainingProgramAsActive.setStatus(2);
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                0,
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
    void shouldThrowApiExceptionUpdateTrainingProgramAsDraftWhenSyllabusNotFound() {
        trainingProgramAsActive.setStatus(2);
        trainingProgramAsActive.setClasses(new HashSet<>());
        Set<TrainingProgramSyllabusDTO> trainingProgramSyllabusDTOSet = new HashSet<>();
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRN211);
        trainingProgramSyllabusDTOSet.add(trainingProgramSyllabusDTOPRO192);
        TrainingProgramReqUpdate trainingProgramReqUpdate = new TrainingProgramReqUpdate(
                1L,
                "Training Program 1",
                0,
                "updating",
                trainingProgramSyllabusDTOSet
        );
        Set<TrainingProgramSyllabus> testSet = new HashSet<>();

        when(trainingProgramRepository.findById(trainingProgramReqUpdate.getId()))
                .thenReturn(Optional.of(trainingProgramAsActive));
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

    TrainingProgramRes mapToDetailRes(TrainingProgram trainingProgram){
        TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
        trainingProgramRes.setId(trainingProgram.getId());
        trainingProgramRes.setTrainingProgramName(trainingProgram.getName());
        trainingProgramRes.setGeneralInformation(trainingProgram.getDescription());
        trainingProgramRes.setDuration(trainingProgram.getDuration());
        trainingProgramRes.setStatus(trainingProgram.getStatus());
        trainingProgramRes.setCreatedBy(trainingProgram.getCreatedBy());
        trainingProgramRes.setCreatedDate(trainingProgram.getCreatedDate());
        trainingProgramRes.setModifiedBy(trainingProgram.getModifiedBy());
        trainingProgramRes.setModifiedDate(trainingProgram.getModifiedDate());
        List<SyllabusContent> syllabusContents = new ArrayList<>();
        for (TrainingProgramSyllabus tps : trainingProgram.getTrainingProgramSyllabusSet()) {
            if (tps.getTrainingProgram().getId().equals(trainingProgram.getId())) {
                Syllabus syllabus = tps.getSyllabus();
                int sequence = tps.getSequence();
                final Float[] totalTimeArray = {0.0F};
                List<DaysUnitRes> daysUnitResList = syllabus.getDaysUnits().stream()
                        .map(dayUnit -> {
                            List<TrainingUnitRes> trainingUnitResList = dayUnit.getTrainingUnits().stream()
                                    .map(trainingUnit -> {
                                        List<TrainingContentRes> trainingContentResList = trainingUnit.getTrainingContents().stream()
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
                                                }).toList();
                                        totalTimeArray[0] = totalTimeArray[0] + trainingUnit.getTrainingTime();
                                        return new TrainingUnitRes(trainingUnit.getId(), trainingUnit.getUnitNumber(),
                                                trainingUnit.getUnitName(), trainingUnit.getTrainingTime(), trainingContentResList);})
                                    .toList();
                            return new DaysUnitRes(dayUnit.getId(), dayUnit.getDayNumber(), trainingUnitResList);})
                        .toList();
                int totalDays = syllabus.getDaysUnits().size();
                Float totalTime = totalTimeArray[0];
                SyllabusContent syllabusContent = new SyllabusContent(sequence, syllabus.getName(), syllabus.getCode(),
                        syllabus.getVersion(), totalTime, totalDays, syllabus.getStatus(), daysUnitResList,
                        syllabus.getCreatedDate(), syllabus.getCreatedBy(), syllabus.getModifiedDate(), syllabus.getModifiedBy());
                syllabusContents.add(syllabusContent);
            }
        }
        trainingProgramRes.setSyllabusContents(syllabusContents);
        trainingProgramRes.getSyllabusContents().sort(Comparator.comparingInt(SyllabusContent::getSequence));
        return trainingProgramRes;
    }
    void assertEqualsTrainingProgramProperties(TrainingProgramRes result, TrainingProgram testExistingTrainingProgram){
        assertEquals(result.getGeneralInformation(), testExistingTrainingProgram.getDescription());
        assertEquals(result.getDuration(), testExistingTrainingProgram.getDuration());
        assertEquals(result.getSyllabusContents().size(), testExistingTrainingProgram.getTrainingProgramSyllabusSet().size());

        for (SyllabusContent expectedResult : result.getSyllabusContents()) {
            assertTrue(testExistingTrainingProgram.getTrainingProgramSyllabusSet().stream()
                    .anyMatch(actualTrainingProgramSyllabus ->
                            actualTrainingProgramSyllabus.getSequence() == expectedResult.getSequence()
                                    && actualTrainingProgramSyllabus.getSyllabus().getName().equals(expectedResult.getSyllabusName())
                                    && actualTrainingProgramSyllabus.getSyllabus().getCode().equals(expectedResult.getSyllabusCode())
                                    && actualTrainingProgramSyllabus.getSyllabus().getVersion().equals(expectedResult.getSyllabusVersion())
                    )
            );
        }
    }
    @Test
    void testSwitchStatusFrom1to0_Success() {
        // Arrange
        Long trainingProgramId = 1L;
        TrainingProgram testTrainingProgram = TrainingProgram.builder()
                .id(trainingProgramId)
                .classes(new HashSet<>())
                .status(1) // Initial status set to active
                .build();
        when(trainingProgramRepository.findById(trainingProgramId)).thenReturn(Optional.of(testTrainingProgram));

        // Set up a mock to return a non-null TrainingProgramRes object
        when(trainingProgramRepository.save(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> {
            TrainingProgram t1 = invocationOnMock.getArgument(0);
            TrainingProgramRes tres = new TrainingProgramRes();
            tres.setId(t1.getId());
            tres.setStatus(t1.getStatus());
            return tres;
        });

        // Act
        TrainingProgramRes result = trainingProgramService.switchStatus(trainingProgramId);

        // Assert
        assertEquals(0, result.getStatus()); // Expected status after toggling

        // Verify that save method is called with the updated training program
        verify(trainingProgramRepository, times(1)).save(testTrainingProgram);
    }

    @Test
    void testSwitchStatusFrom0to1_Success() {
        // Arrange
        Long trainingProgramId = 1L;
        TrainingProgram testTrainingProgram = TrainingProgram.builder()
                .id(trainingProgramId)
                .status(0) // Initial status set to active
                .classes(new HashSet<>())
                .build();
        when(trainingProgramRepository.findById(trainingProgramId)).thenReturn(Optional.of(testTrainingProgram));

        // Set up a mock to return a non-null TrainingProgramRes object
        when(trainingProgramRepository.save(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> {
            TrainingProgram t1 = invocationOnMock.getArgument(0);
            TrainingProgramRes tres = new TrainingProgramRes();
            tres.setId(t1.getId());
            tres.setStatus(t1.getStatus());
            return tres;
        });

        // Act
        TrainingProgramRes result = trainingProgramService.switchStatus(trainingProgramId);

        // Assert
        assertEquals(1, result.getStatus()); // Expected status after toggling

        // Verify that save method is called with the updated training program
        verify(trainingProgramRepository, times(1)).save(testTrainingProgram);
    }

    @Test
    void testSwitchStatus_DraftingStatus() {
        // Arrange
        Long trainingProgramId = 1L;
        TrainingProgram testTrainingProgram = TrainingProgram.builder()
                .id(trainingProgramId)
                .status(2) // Drafting status
                .classes(new HashSet<>())
                .build();
        when(trainingProgramRepository.findById(trainingProgramId)).thenReturn(Optional.of(testTrainingProgram));

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> trainingProgramService.switchStatus(trainingProgramId),
                "Expected ApiException to be thrown for drafting status");

        assertTrue(exception.getMessage().contains("Error set de-activate/activate for training program with DRAFTING status"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }


    @Test
    void should_successfully_duplicateTrainingProgram() {
        Long newId = 2L;

        // Mocking security context
        Authentication authentication = new UsernamePasswordAuthenticationToken("mockUser", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock data
        Users user = new Users();
        user.setName("MockUser");
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(user));


        when(trainingProgramSyllabusRepository.save(any(TrainingProgramSyllabus.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(trainingProgramRepository.findById(anyLong())).thenReturn(Optional.of(testExistingTrainingProgram));
        when(trainingProgramRepository.findByName(any())).thenReturn(Optional.empty()); // Assuming the name is not already taken
        when(trainingProgramRepository.save(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> {
            TrainingProgram newTrainingProgram = invocationOnMock.getArgument(0);
            newTrainingProgram.setId(newId);
            return mapToDetailRes(newTrainingProgram);
        });

        // Invoke the method
        TrainingProgramRes result = trainingProgramService.duplicateTrainingProgram(testExistingTrainingProgram.getId());

        // Assertions
        assertNotNull(result);
        verify(trainingProgramRepository, times(1)).findById(anyLong());
        verify(trainingProgramRepository, times(1)).findByName(anyString());
        verify(trainingProgramConverter, times(1)).convertToTrainingProgramRes(any(TrainingProgram.class));
        assertEquals(testExistingTrainingProgram.getName() + "_1", result.getTrainingProgramName());
        assertEqualsTrainingProgramProperties(result, testExistingTrainingProgram);
        assertEquals(result.getId(), newId);
        assertEquals(result.getStatus(), 2);
        assertNotNull("MockUser", result.getCreatedBy());
        assertNotNull(result.getCreatedDate());
        // Clear security context
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testGetActiveTrainingProgramListWithNameNull() {
        // Mock the repository response
        TrainingProgram mockProgram = new TrainingProgram(); // Assume this is your entity class
        when(trainingProgramRepository.findAllByStatus(1))
                .thenReturn(Arrays.asList(mockProgram));

        // Mock the converter response
        TrainingProgramDTO mockDto = new TrainingProgramDTO(); // Assume this is your DTO class
        when(trainingProgramConverter.convertToPageRes(any(TrainingProgram.class)))
                .thenReturn(mockDto);

        // Test the method with name = null
        List<TrainingProgramDTO> resultList = trainingProgramService.getActiveTrainingProgramList(null);

        // Assertions
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(mockDto, resultList.get(0));

        // Verify interaction
        verify(trainingProgramRepository).findAllByStatus(1);
        verify(trainingProgramConverter).convertToPageRes(mockProgram);
    }

    @Test
    public void testGetActiveTrainingProgramListWithNameNotNull() {
        // Mock the repository response
        String name = "TestName";
        TrainingProgram mockProgram = new TrainingProgram(); // Assume this is your entity
        when(trainingProgramRepository.findAllByNameContainingIgnoreCaseAndStatus(name, 1))
                .thenReturn(Arrays.asList(mockProgram));

        // Mock the converter response
        TrainingProgramDTO mockDto = new TrainingProgramDTO(); // Assume this is your DTO
        when(trainingProgramConverter.convertToPageRes(any(TrainingProgram.class)))
                .thenReturn(mockDto);

        // Test the method with a non-null name
        List<TrainingProgramDTO> resultList = trainingProgramService.getActiveTrainingProgramList(name);

        // Assertions
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(mockDto, resultList.get(0));

        // Verify interaction
        verify(trainingProgramRepository).findAllByNameContainingIgnoreCaseAndStatus(name, 1);
        verify(trainingProgramConverter).convertToPageRes(mockProgram);
    }

    private void mockUserWithRole(int roleId) {
        Users mockUser = new Users();
        Role role = new Role();
        role.setRoleId(roleId);
        mockUser.setRole(role);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("user@example.com");
        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
    }

    @Test
    void whenUserNotFound_thenThrowException() {
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("nonexistent@example.com");
        when(usersRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () ->
                trainingProgramService.searchTrainingPrograms("keyword", null, null, null, null, null, PageRequest.of(0, 10)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User not log in or not found!", exception.getMessage());
    }

    @Test
    void whenStartDateNotNullAndEndDateNull_thenThrowException() {
        mockUserWithRole(1); // Helper method to mock user authentication and role

        ApiException exception = assertThrows(ApiException.class, () ->
                trainingProgramService.searchTrainingPrograms("keyword", null, LocalDate.now(), null, null, null, PageRequest.of(0, 10)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("End Date must not be null or empty", exception.getMessage());
    }

    @Test
    void whenEndDateNotNullAndStartDateNull_thenThrowException() {
        mockUserWithRole(1); // Assuming a helper method to mock user

        ApiException exception = assertThrows(ApiException.class, () ->
                trainingProgramService.searchTrainingPrograms("keyword", null, null, LocalDate.now(), null, null, PageRequest.of(0, 10)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Start Date must not be null or empty", exception.getMessage());
    }

    @Test
    void whenDatesProvidedAndUserRoleNot3_thenSuccess() {
        mockUserWithRole(2); // Not role 3, adjust mockUserWithRole accordingly

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(5);
        Page<TrainingProgram> mockedPage = new PageImpl<>(Arrays.asList(new TrainingProgram()));
        when(trainingProgramRepository.searchWithDateNotNull(any(), any(), eq(startDate), eq(endDate), any(), any(), any()))
                .thenReturn(mockedPage);

        Page<TrainingProgramDTO> result = trainingProgramService.searchTrainingPrograms("keyword", null, startDate, endDate, 10, Arrays.asList(1), PageRequest.of(0, 10));

        assertNotNull(result);
        verify(trainingProgramRepository).searchWithDateNotNull(any(), any(), eq(startDate), eq(endDate), any(), any(), any());
    }

    @Test
    void whenDatesProvidedAndUserRole3_thenSuccess() {
        mockUserWithRole(3); // Role 3, specific for this case

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(5);
        Page<TrainingProgram> mockedPage = new PageImpl<>(Arrays.asList(new TrainingProgram()));
        when(trainingProgramRepository.searchWithDateNotNullWithStatusActive(any(), any(), eq(startDate), eq(endDate), any(), any(), any()))
                .thenReturn(mockedPage);

        Page<TrainingProgramDTO> result = trainingProgramService.searchTrainingPrograms("keyword", null, startDate, endDate, 10, Arrays.asList(1), PageRequest.of(0, 10));

        assertNotNull(result);
        verify(trainingProgramRepository).searchWithDateNotNullWithStatusActive(any(), any(), eq(startDate), eq(endDate), any(), any(), any());
    }

    @Test
    void whenNoDatesProvidedAndRoleNot3_thenSearchWithDateNull() {
        // Setup user with a role that's not 3
        mockUserWithRole(2); // Assuming role 2 is a general user/admin

        Page<TrainingProgram> mockedPage = new PageImpl<>(Collections.emptyList());
        when(trainingProgramRepository.searchWithDateNull(anyString(), anyList(), anyInt(), anyList(), any(Pageable.class)))
                .thenReturn(mockedPage);

        // Execute
        Page<TrainingProgramDTO> result = trainingProgramService.searchTrainingPrograms("keyword", Arrays.asList("creator"), null, null, 10, Arrays.asList(1), PageRequest.of(0, 10));

        // Verify that the correct repository method is called
        verify(trainingProgramRepository).searchWithDateNull(anyString(), anyList(), anyInt(), anyList(), any(Pageable.class));
        assertNotNull(result);
    }

    @Test
    void whenNoDatesProvidedAndRoleIs3_thenSearchWithDateNullWithStatusActive() {
        // Setup user with role 3
        mockUserWithRole(3); // Assuming role 3 has specific restrictions or permissions

        Page<TrainingProgram> mockedPage = new PageImpl<>(Collections.emptyList());
        when(trainingProgramRepository.searchWithDateNullWithStatusActive(anyString(), anyList(), anyInt(), anyList(), any(Pageable.class)))
                .thenReturn(mockedPage);

        // Execute
        Page<TrainingProgramDTO> result = trainingProgramService.searchTrainingPrograms("keyword", Arrays.asList("creator"), null, null, 10, Arrays.asList(1), PageRequest.of(0, 10));

        // Verify that the correct repository method is called for role 3
        verify(trainingProgramRepository).searchWithDateNullWithStatusActive(anyString(), anyList(), anyInt(), anyList(), any(Pageable.class));
        assertNotNull(result);
    }
}
