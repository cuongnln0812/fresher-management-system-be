package com.example.phase1_fams.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.TrainingProgramConverter;
import com.example.phase1_fams.dto.*;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.*;
import com.example.phase1_fams.dto.response.*;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.model.Class;
import com.example.phase1_fams.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.phase1_fams.converter.ClassConverter;
import com.example.phase1_fams.converter.SessionConverter;
import com.example.phase1_fams.dto.ClassGeneralDTO;
import com.example.phase1_fams.model.Session;
import com.example.phase1_fams.repository.ClassRepository;
import com.example.phase1_fams.service.impl.ClassServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

public class ClassServiceTest {
    @Mock
    private ClassRepository classRepository;

    @Mock
    private ClassConverter classConverter;

    @Mock
    private SessionConverter sessionConverter;

    @InjectMocks
    private ClassServiceImpl classServiceImpl;

    @Mock
    private TrainingProgramConverter trainingProgramConverter;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ClassUserRepository classUserRepository;
    @Mock
    private UsersRepository usersRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private TrainingProgramRepository trainingProgramRepository;

    Class class1;
    Class class2;
    Class class3;
    ClassGeneralDTO class2GeneralDTO;
    ClassGeneralDTO class1GeneralDTO;
    Session session2;
    SessionRes session2Res;
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
    Users testUser;
    Role admin;
    Class testClass;
    Session testSession;

    ClassUserReq testClassUserReq;
    ClassReq testClassReq;
    SessionReq testTrainingCalendarDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init() {
        class1 = Class.builder().name("Class Name1").code("ClassCode1").fsu("ClassFSU1").id(1L)
                .status("Planning")
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(11, 0))
                .location("Location1").build();
        class2 = Class.builder().name("Class Name2").code("ClassCode2").fsu("CLassFSU2").id(2L)
                .status("Scheduled")
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(15, 0))
                .location("Location2")
                .build();
        class3 = Class.builder().name("Class Name3").code("ClassCode3").fsu("CLassFSU3").id(3L)
                .status("Opening")
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(21, 0))
                .location("Location3")
                .build();
        class2GeneralDTO = ClassGeneralDTO.builder()
                .fsu(class2.getFsu())
                .build();
        class1GeneralDTO = ClassGeneralDTO.builder()
                .fsu(class1.getFsu())
                .build();
        session2 = session2.builder()
                .location(class2.getLocation())
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
                .trainingTime(60F) // Assuming time is in minutes
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
                .duration(2)
                .status(1)
                .classes(new HashSet<>())
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();
        admin = Role.builder()
                .roleId(1)
                .roleName("SUPPER_ADMIN")
                .syllabusPermissionGroup(SyllabusPermissionGroup.FULL_ACCESS)
                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.FULL_ACCESS)
                .classPermissionGroup(ClassPermissionGroup.FULL_ACCESS)
                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.FULL_ACCESS)
                .userPermissionGroup(UserPermissionGroup.FULL_ACCESS)
                .build();
        testUser = Users.builder()
                .id(2L)
                .name("John Doe")
                .email("john.doe@example.com")
                .dob(LocalDate.of(2003, 11, 21))
                .phone("1234567890")
                .gender("Male")
                .isFirstLogin(true)
                .status(true)
                .role(admin)
                .build();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse("07:00", formatter);
        LocalTime endTime = LocalTime.parse("09:00", formatter);
        testClass = Class.builder()
                .id(1L)
                .code("HCM2401")
                .name("Java Class")
                .duration(2)
                .location("HCM")
                .fsu("ftown1")
                .startTime(startTime)
                .endTime(endTime)
                .status("scheduled")
                .createdBy("admin")
                .build();
        testSession = Session.builder()
                .id(1L)
                .dayProgress(1)
                .totalDays(2)
                .location("HCM")
                .trainerName("trainer")
                .adminName("admin")
                .sessionDate(LocalDate.of(2024, 3, 1))
                .startTime(startTime)
                .endTime(endTime)
                .aClass(testClass)
                .build();
        testClassUserReq = ClassUserReq.builder()
                .userId(1L)
                .userType("admin")
                .build();
        testClassReq = ClassReq.builder()
                .locationCode("HCM")
                .name("Java Class")
                .location("HCM")
                .fsu("ftown1")
                .startTime("07:00")
                .endTime("09:00")
                .trainingProgramId(1L)
                .build();
        class2 = Class.builder().name("Class Name2").code("ClassCode2").fsu("CLassFSU2").id(2L).status("Scheduled")
                .location("Location2").build();

//        testTrainingCalendarDto = SessionReq.builder()
//                .start(String.valueOf(LocalTime.of(8, 0)))
//                .end(String.valueOf(LocalTime.of(11, 0)))
//                .sessionDate(LocalDate.now())
//                .build();

    }

        @Test
        void testUpdateStatusPlanning() {
                Long classId = 1L;
                when(classRepository.findById(classId)).thenReturn(Optional.of(class1));
                Class savedClass = Class.builder().name("Class Name1").code("ClassCode1").fsu("ClassFSU1").id(1L)
                                .status("Inactive").build();
                when(classRepository.save(any(Class.class))).thenReturn(savedClass);
                classServiceImpl.deactivateClass(classId);
                verify(classRepository).save(any(Class.class)); // Ensure that save was called
                ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
                verify(classRepository).save(classCaptor.capture()); // Capture the argument passed to save
                assertEquals("Inactive", classCaptor.getValue().getStatus()); // Assert the status

        }

        @Test
        void testUpdateStatusScheduled() {
                Long classId = 2L;
                when(classRepository.findById(classId)).thenReturn(Optional.of(class2));
                Class savedClass = Class.builder().name("Class Name2").code("ClassCode2").fsu("ClassFSU2").id(2L)
                                .status("Inactive").build();
                when(classRepository.save(any(Class.class))).thenReturn(savedClass);
                classServiceImpl.deactivateClass(classId);
                verify(classRepository).save(any(Class.class)); // Ensure that save was called
                ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
                verify(classRepository).save(classCaptor.capture()); // Capture the argument passed to save
                assertEquals("Inactive", classCaptor.getValue().getStatus()); // Assert the status

    }

    @Test
    void testUpdateStatusFail() {
            Long classId = 3L;
            when(classRepository.findById(classId)).thenReturn(Optional.of(class3));
            assertThrows(RuntimeException.class, () -> {
                    classServiceImpl.deactivateClass(classId);
            });
            verify(classRepository, never()).save(any());

    }

    @Test
    void testViewDetail() {
            Long ClassId = 2L;
            when(classRepository.findById(ClassId)).thenReturn(Optional.of(class2));

            ClassDetailsRes classDetailsRes = ClassDetailsRes.builder()
                            .className(class2.getName())
                            .classId(class2.getId())
                            .status(class2.getStatus())
                            .classGeneralDTO(class2GeneralDTO)
                            .build();
            when(classConverter.convertToDetailsRes(class2)).thenReturn(classDetailsRes);
            classServiceImpl.getClassDetails(ClassId);
            verify(classRepository, times(1)).findById(ClassId);
            assertNotNull(classDetailsRes);
    }

    @Test
    public void testCreateClassAsScheduled_Successfully() {

        TrainingProgramSyllabusKey oldKey = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                oldKey,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(testClassUserReq);
        testClassReq.setAttendeeDTO(attendeeDTO);
        testClassReq.setListOfSessionDate(listOfSessionDate);
        testClassReq.setClassUserDTOSet(classUserReqSet);
        testClassReq.setStartTime("08:00");
        testClassReq.setEndTime("11:00");
        List<Class> classList = new ArrayList<>();
        classList.add(testClass);
        ClassUserKey key = new ClassUserKey(testUser.getId(), testClass.getId());
        ClassUser classUser = new ClassUser(
                key,
                testUser,
                testClass,
                "admin"
        );
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(classUser);
        testClass.setClassUserType(classUserSet);

        Set<Session> sessionSet = new HashSet<>();
        sessionSet.add(testSession);

        when(classRepository.findAll())
                .thenReturn(classList);
        when(trainingProgramRepository.findByIdAndStatus(testClassReq.getTrainingProgramId(), 1))
                .thenReturn(Optional.of(testTrainingProgram));

        when(classRepository.save(any(Class.class)))
                .thenAnswer(invocationOnMock -> {
                    Class dto = new Class();
                    dto.setId(1L);
                    dto.setCode("HCM2401");
                    dto.setName("Java Class");
                    dto.setDuration(2);
                    dto.setLocation("HCM");
                    dto.setFsu("ftown1");
                    dto.setStatus("scheduled");
                    dto.setCreatedBy("admin");
                    dto.setPlannedAttendee(10);
                    dto.setActualAttendee(10);
                    dto.setAcceptedAttendee(10);
                    dto.setTrainingProgram(testTrainingProgram);
                    dto.setClassUserType(classUserSet);
                    dto.setSessions(sessionSet);
                    return dto;
                });
        when(usersRepository.findById(testClassUserReq.getUserId()))
                .thenReturn(Optional.of(testUser));
        when(classUserRepository.save(any(ClassUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.save(any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(authenticationService.getName())
                .thenReturn("admin");
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
                    trainingProgramRes.setId(1L);
                    trainingProgramRes.setTrainingProgramName("Training Program 1");
                    trainingProgramRes.setDuration(2);
                    trainingProgramRes.setStatus(1);
                    trainingProgramRes.setCreatedBy("admin");
                    trainingProgramRes.setGeneralInformation("updating");
                    trainingProgramRes.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    return trainingProgramRes;
                });

        ClassRes classRes = classServiceImpl.createClassAsScheduled(testClassReq);

        assertNotNull(classRes);
        assertEquals(testClassReq.getName(), classRes.getName());
        assertEquals(testClassReq.getFsu(), classRes.getFsu());
        assertEquals(testClassReq.getLocation(), classRes.getLocation());
        assertEquals(testClassReq.getAttendeeDTO().getPlanned(), classRes.getAttendeeDTO().getPlanned());
        assertEquals(testClassReq.getAttendeeDTO().getAccepted(), classRes.getAttendeeDTO().getAccepted());
        assertEquals(testClassReq.getAttendeeDTO().getActual(), classRes.getAttendeeDTO().getActual());

        verify(classRepository, times(1))
                .findAll();
        verify(trainingProgramRepository, times(1))
                .findByIdAndStatus(testClassReq.getTrainingProgramId(), 1);
        verify(classRepository, times(2))
                .save(any(Class.class));
        verify(usersRepository, times(1))
                .findById(testClassUserReq.getUserId());
        verify(classUserRepository, times(1))
                .save(any(ClassUser.class));
        verify(sessionRepository, times(2))
                .save(any(Session.class));
        verify(authenticationService, times(1))
                .getName();
        verify(trainingProgramConverter, times(1))
                .convertToTrainingProgramRes(any(TrainingProgram.class));
    }



    @Test
    public void testUpdateClassAsScheduled_Successfully() {
        Long classId = 1L;
        ClassReqUpdate updatedClassReq = new ClassReqUpdate();
        updatedClassReq.setName("Java Class");
        updatedClassReq.setLocation("HCM");
        updatedClassReq.setLocationCode("HCM01");
        updatedClassReq.setStartTime("08:00");
        updatedClassReq.setEndTime("11:00");
        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        updatedClassReq.setAttendeeDTO(attendeeDTO);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        updatedClassReq.setListOfSessionDate(listOfSessionDate);
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(new ClassUserReq(1L, "admin"));
        updatedClassReq.setClassUserDTOSet(classUserReqSet);
        updatedClassReq.setTrainingProgramId(1L);
        TrainingProgram newTrainingProgram = new TrainingProgram();
        newTrainingProgram.setDuration(2);
        Class existingClass = new Class();
        existingClass.setId(1L);
        existingClass.setStatus("Planning");
        existingClass.setDuration(0);
        existingClass.setTrainingProgram(new TrainingProgram());
        existingClass.setCode("HCM01_2024_01");

        when(authenticationService.getName()).thenReturn("MockUser");
        when(classRepository.findById(classId)).thenReturn(Optional.of(existingClass));
        when(trainingProgramRepository.findByIdAndStatus(anyLong(), anyInt())).thenReturn(Optional.of(newTrainingProgram));
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(new Users()));
        when(classUserRepository.save(any(ClassUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authenticationService.getName()).thenReturn("admin");
        when(classRepository.save(any(Class.class))).thenReturn(existingClass);

            // Ensure updateClassAsScheduled method handles null return value gracefully
        ClassRes updatedClassRes = classServiceImpl.updateClassAsScheduled(classId, updatedClassReq);
        assertNotNull(updatedClassRes, "Updated class response should not be null");
        assertEquals("Scheduled", updatedClassRes.getStatus());
        assertNotNull(updatedClassRes.getId());


        // Additional assertions for the generated code
        assertNotNull("Generated code should not be null", existingClass.getCode());
        assertTrue(existingClass.getCode().startsWith("HCM01_"), "Generated code should start with 'HCM01_'");
    }

    private String generateUniqueCode(String locationCode) {
        String generatedCode = locationCode.toUpperCase() + "_" + generateRandomNumber();
        return generatedCode;
    }

    private String generateRandomNumber() {
        return String.valueOf((int)(Math.random() * 100));
    }

    private String generateNewCode(String locationCode) {
        return locationCode.toUpperCase() + "_NEW_CODE";
    }



    private ClassReqUpdate createValidClassReqUpdate() {
        ClassReqUpdate classReqUpdate = new ClassReqUpdate();
        classReqUpdate.setName("Java Class");
        classReqUpdate.setLocation("HCM");
        classReqUpdate.setLocationCode("HCM01");
        classReqUpdate.setStartTime("08:00");
        classReqUpdate.setEndTime("11:00");
        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        classReqUpdate.setAttendeeDTO(attendeeDTO);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        classReqUpdate.setListOfSessionDate(listOfSessionDate);
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(new ClassUserReq(1L, "admin"));
        classReqUpdate.setClassUserDTOSet(classUserReqSet);
        classReqUpdate.setTrainingProgramId(1L);
        return classReqUpdate;
    }

    @Test
    public void testUpdateClassAsScheduled_Failed() {
        Class existingClass = new Class();
        existingClass.setStatus("InProgress");
        when(classRepository.findById(anyLong())).thenReturn(Optional.of(existingClass));
        ClassReqUpdate updatedClassReq = createValidClassReqUpdate();
        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.updateClassAsScheduled(1L, updatedClassReq));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Class cannot be updated because its status is not 'Planning' or 'Scheduled'", exception.getMessage());
    }

    @Test
    public void testValidateForScheduled_ValidInput() {
        ClassReqUpdate validClassReqUpdate = createValidClassReqUpdate();

        assertDoesNotThrow(() -> classServiceImpl.validateForScheduled(validClassReqUpdate));
    }

    @Test
    public void testValidateForScheduled_MissingName() {
        ClassReqUpdate classReqUpdate = createValidClassReqUpdate();
        classReqUpdate.setName(null);

        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.validateForScheduled(classReqUpdate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Name is required", exception.getMessage());
    }


    @Test
    public void testValidateForScheduled_MissingLocation() {
        ClassReqUpdate classReqUpdate = createValidClassReqUpdate();
        classReqUpdate.setLocation(null);

        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.validateForScheduled(classReqUpdate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Location is required", exception.getMessage());
    }

    @Test
    public void testValidateForScheduled_MissingLocationCode() {
        ClassReqUpdate classReqUpdate = createValidClassReqUpdate();
        classReqUpdate.setLocationCode(null);

        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.validateForScheduled(classReqUpdate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Location code is required", exception.getMessage());
    }


    @Test
    public void testValidateForScheduled_MissingAttendeeDTO() {
        ClassReqUpdate classReqUpdate = createValidClassReqUpdate();
        classReqUpdate.setAttendeeDTO(null);

        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.validateForScheduled(classReqUpdate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("AttendeeDTO is required", exception.getMessage());
    }

    @Test
    public void testValidateForScheduled_NoSessionDate() {
        ClassReqUpdate classReqUpdate = createValidClassReqUpdate();
        classReqUpdate.setListOfSessionDate(Collections.emptyList());

        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.validateForScheduled(classReqUpdate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("At least one session date is required", exception.getMessage());
    }

    @Test
    public void testValidateForScheduled_MissingTrainingProgram() {
        ClassReqUpdate classReqUpdate = createValidClassReqUpdate();
        classReqUpdate.setTrainingProgramId(0L);

        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.validateForScheduled(classReqUpdate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Training Program is required", exception.getMessage());
    }


    @Test
    public void testUpdateClassAsPlanning() {
        Long classId = 1L;
        ClassReqUpdate updatedClassReq = new ClassReqUpdate();
        updatedClassReq.setName("Sample Class");
        updatedClassReq.setLocation("Sample Location");
        updatedClassReq.setLocationCode("LOC001");
        updatedClassReq.setStartTime("09:00");
        updatedClassReq.setEndTime("10:00");
        updatedClassReq.setAttendeeDTO(new AttendeeDTO());
        updatedClassReq.setListOfSessionDate(Collections.singletonList(LocalDate.now()));
        updatedClassReq.setTrainingProgramId(1L);

        TrainingProgram trainingProgram = new TrainingProgram();

        Class existingClass = new Class();
        existingClass.setStatus("Planning");
        existingClass.setTrainingProgram(trainingProgram);

        existingClass.setCode("SomeNonEmptyCode");

        when(authenticationService.getName()).thenReturn("MockUser");
        when(classRepository.findById(classId)).thenReturn(Optional.of(existingClass));
        when(trainingProgramRepository.findByIdAndStatus(1L, 1)).thenReturn(Optional.of(trainingProgram));
        when(classRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRes result = classServiceImpl.updateClassAsPlanning(classId, updatedClassReq);

        assertNotNull(result);
        assertEquals("Planning", result.getStatus());
        assertEquals(updatedClassReq.getName(), result.getName());
        assertEquals(updatedClassReq.getLocation(), result.getLocation());
    }

    @Test
    public void testCreateClassAsScheduled_TrainingProgramIsNotFound() {

        TrainingProgramSyllabusKey oldKey = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                oldKey,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(testClassUserReq);
        testClassReq.setAttendeeDTO(attendeeDTO);
        testClassReq.setListOfSessionDate(listOfSessionDate);
        testClassReq.setClassUserDTOSet(classUserReqSet);
        testClassReq.setStartTime("08:00");
        testClassReq.setEndTime("11:00");
        List<Class> classList = new ArrayList<>();
        classList.add(testClass);
        ClassUserKey key = new ClassUserKey(testUser.getId(), testClass.getId());
        ClassUser classUser = new ClassUser(
                key,
                testUser,
                testClass,
                "admin"
        );
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(classUser);
        testClass.setClassUserType(classUserSet);

        Set<Session> sessionSet = new HashSet<>();
        sessionSet.add(testSession);

        when(authenticationService.getName()).thenReturn("MockUser");
        when(classRepository.findAll())
                .thenReturn(classList);
        when(trainingProgramRepository.findByIdAndStatus(testClassReq.getTrainingProgramId(), 1))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            classServiceImpl.createClassAsScheduled(testClassReq);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cannot find this training program with status Active", exception.getMessage());
    }

    @Test
    public void testCreateClassAsScheduled_UserIsNotFound() {

        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(testClassUserReq);
        testClassReq.setAttendeeDTO(attendeeDTO);
        testClassReq.setListOfSessionDate(listOfSessionDate);
        testClassReq.setClassUserDTOSet(classUserReqSet);
        testClassReq.setStartTime("08:00");
        testClassReq.setEndTime("11:00");
        List<Class> classList = new ArrayList<>();
        classList.add(testClass);
        ClassUserKey key = new ClassUserKey(testUser.getId(), testClass.getId());
        ClassUser classUser = new ClassUser(
                key,
                testUser,
                testClass,
                "admin"
        );
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(classUser);
        testClass.setClassUserType(classUserSet);

        Set<Session> sessionSet = new HashSet<>();
        sessionSet.add(testSession);

        when(classRepository.findAll())
                .thenReturn(classList);
        when(trainingProgramRepository.findByIdAndStatus(testClassReq.getTrainingProgramId(), 1))
                .thenReturn(Optional.of(testTrainingProgram));

        when(classRepository.save(any(Class.class)))
                .thenAnswer(invocationOnMock -> {
                    Class dto = new Class();
                    dto.setId(1L);
                    dto.setCode("HCM2401");
                    dto.setName("Java Class");
                    dto.setDuration(2);
                    dto.setLocation("HCM");
                    dto.setFsu("ftown1");
                    dto.setStatus("scheduled");
                    dto.setCreatedBy("admin");
                    dto.setPlannedAttendee(10);
                    dto.setActualAttendee(10);
                    dto.setAcceptedAttendee(10);
                    dto.setTrainingProgram(testTrainingProgram);
                    dto.setClassUserType(classUserSet);
                    dto.setSessions(sessionSet);
                    return dto;
                });
        when(usersRepository.findById(testClassUserReq.getUserId()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            classServiceImpl.createClassAsScheduled(testClassReq);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cannot find this user", exception.getMessage());

    }

    @Test
    public void testCreateClassAsPlanning_Successfully() {

        TrainingProgramSyllabusKey oldKey = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                oldKey,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(testClassUserReq);
        testClassReq.setAttendeeDTO(attendeeDTO);
        testClassReq.setListOfSessionDate(listOfSessionDate);
        testClassReq.setClassUserDTOSet(classUserReqSet);
        testClassReq.setStartTime("08:00");
        testClassReq.setEndTime("11:00");
        List<Class> classList = new ArrayList<>();
        classList.add(testClass);
        ClassUserKey key = new ClassUserKey(testUser.getId(), testClass.getId());
        ClassUser classUser = new ClassUser(
                key,
                testUser,
                testClass,
                "admin"
        );
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(classUser);
        testClass.setClassUserType(classUserSet);

        Set<Session> sessionSet = new HashSet<>();
        sessionSet.add(testSession);

        when(classRepository.findAll())
                .thenReturn(classList);
        when(trainingProgramRepository.findByIdAndStatus(testClassReq.getTrainingProgramId(), 1))
                .thenReturn(Optional.of(testTrainingProgram));

        when(classRepository.save(any(Class.class)))
                .thenAnswer(invocationOnMock -> {
                    Class dto = new Class();
                    dto.setId(1L);
                    dto.setCode("HCM2401");
                    dto.setName("Java Class");
                    dto.setDuration(2);
                    dto.setLocation("HCM");
                    dto.setFsu("ftown1");
                    dto.setStatus("planning");
                    dto.setCreatedBy("admin");
                    dto.setPlannedAttendee(10);
                    dto.setActualAttendee(10);
                    dto.setAcceptedAttendee(10);
                    dto.setTrainingProgram(testTrainingProgram);
                    dto.setClassUserType(classUserSet);
                    dto.setSessions(sessionSet);
                    return dto;
                });
        when(usersRepository.findById(testClassUserReq.getUserId()))
                .thenReturn(Optional.of(testUser));
        when(classUserRepository.save(any(ClassUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.save(any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(authenticationService.getName())
                .thenReturn("admin");
        when(trainingProgramConverter.convertToTrainingProgramRes(any(TrainingProgram.class)))
                .thenAnswer(invocation -> {
                    TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
                    trainingProgramRes.setId(1L);
                    trainingProgramRes.setTrainingProgramName("Training Program 1");
                    trainingProgramRes.setDuration(2);
                    trainingProgramRes.setStatus(1);
                    trainingProgramRes.setCreatedBy("admin");
                    trainingProgramRes.setGeneralInformation("updating");
                    trainingProgramRes.setCreatedDate(LocalDate.of(2024, Calendar.FEBRUARY, 23));
                    return trainingProgramRes;
                });

        ClassRes classRes = classServiceImpl.createClassAsPlanning(testClassReq);

        assertNotNull(classRes);
        assertEquals(testClassReq.getName(), classRes.getName());
        assertEquals(testClassReq.getFsu(), classRes.getFsu());
        assertEquals(testClassReq.getLocation(), classRes.getLocation());
        assertEquals(testClassReq.getAttendeeDTO().getPlanned(), classRes.getAttendeeDTO().getPlanned());
        assertEquals(testClassReq.getAttendeeDTO().getAccepted(), classRes.getAttendeeDTO().getAccepted());
        assertEquals(testClassReq.getAttendeeDTO().getActual(), classRes.getAttendeeDTO().getActual());

        verify(classRepository, times(1))
                .findAll();
        verify(trainingProgramRepository, times(1))
                .findByIdAndStatus(testClassReq.getTrainingProgramId(), 1);
        verify(classRepository, times(2))
                .save(any(Class.class));
        verify(usersRepository, times(1))
                .findById(testClassUserReq.getUserId());
        verify(classUserRepository, times(1))
                .save(any(ClassUser.class));
        verify(sessionRepository, times(2))
                .save(any(Session.class));
        verify(authenticationService, times(1))
                .getName();
        verify(trainingProgramConverter, times(1))
                .convertToTrainingProgramRes(any(TrainingProgram.class));
    }

    @Test
    public void testCreateClassAsPlanning_TrainingProgramIsNotFound() {

        TrainingProgramSyllabusKey oldKey = new TrainingProgramSyllabusKey(testSyllabus.getCode(), testTrainingProgram.getId());
        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus(
                oldKey,
                testSyllabus,
                testTrainingProgram,
                1
        );

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);
        testTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(testClassUserReq);
        testClassReq.setAttendeeDTO(attendeeDTO);
        testClassReq.setStartTime("08:00");
        testClassReq.setEndTime("11:00");
        testClassReq.setListOfSessionDate(listOfSessionDate);
        testClassReq.setClassUserDTOSet(classUserReqSet);
        List<Class> classList = new ArrayList<>();
        classList.add(testClass);
        ClassUserKey key = new ClassUserKey(testUser.getId(), testClass.getId());
        ClassUser classUser = new ClassUser(
                key,
                testUser,
                testClass,
                "admin"
        );
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(classUser);
        testClass.setClassUserType(classUserSet);

        Set<Session> sessionSet = new HashSet<>();
        sessionSet.add(testSession);

        when(classRepository.findAll())
                .thenReturn(classList);
        when(trainingProgramRepository.findById(testClassReq.getTrainingProgramId()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            classServiceImpl.createClassAsPlanning(testClassReq);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cannot find this training program", exception.getMessage());
    }

    @Test
    public void testCreateClassAsPlanning_UserIsNotFound() {

        AttendeeDTO attendeeDTO = new AttendeeDTO("Fresher", 10, 10, 10);
        List<LocalDate> listOfSessionDate = new ArrayList<>();
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 1));
        listOfSessionDate.add(LocalDate.of(2024, Calendar.MARCH, 10));
        Set<ClassUserReq> classUserReqSet = new HashSet<>();
        classUserReqSet.add(testClassUserReq);
        testClassReq.setAttendeeDTO(attendeeDTO);
        testClassReq.setListOfSessionDate(listOfSessionDate);
        testClassReq.setClassUserDTOSet(classUserReqSet);
        testClassReq.setStartTime("08:00");
        testClassReq.setEndTime("11:00");
        List<Class> classList = new ArrayList<>();
        classList.add(testClass);
        ClassUserKey key = new ClassUserKey(testUser.getId(), testClass.getId());
        ClassUser classUser = new ClassUser(
                key,
                testUser,
                testClass,
                "admin"
        );
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(classUser);
        testClass.setClassUserType(classUserSet);

        Set<Session> sessionSet = new HashSet<>();
        sessionSet.add(testSession);

        when(classRepository.findAll())
                .thenReturn(classList);
        when(trainingProgramRepository.findByIdAndStatus(testClassReq.getTrainingProgramId(), 1))
                .thenReturn(Optional.of(testTrainingProgram));

        when(classRepository.save(any(Class.class)))
                .thenAnswer(invocationOnMock -> {
                    Class dto = new Class();
                    dto.setId(1L);
                    dto.setCode("HCM2401");
                    dto.setName("Java Class");
                    dto.setDuration(2);
                    dto.setLocation("HCM");
                    dto.setFsu("ftown1");
                    dto.setStatus("planning");
                    dto.setCreatedBy("admin");
                    dto.setPlannedAttendee(10);
                    dto.setActualAttendee(10);
                    dto.setAcceptedAttendee(10);
                    dto.setTrainingProgram(testTrainingProgram);
                    dto.setClassUserType(classUserSet);
                    dto.setSessions(sessionSet);
                    return dto;
                });
        when(usersRepository.findById(testClassUserReq.getUserId()))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            classServiceImpl.createClassAsPlanning(testClassReq);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cannot find this user", exception.getMessage());

    }




//    @Test
//    public void testUpdateAllSessionsInClass_successfully() {
//        // Mock data
//        Long classId = 1L;
//        SessionReq sessionReq = new SessionReq();
//        sessionReq.setStart("09:00");
//        sessionReq.setEnd("11:00");
//        sessionReq.setListOfSessionDate(Collections.singletonList(LocalDate.now()));
//        sessionReq.setFsu("F-Town 2");
//        sessionReq.setAdminId(1L);
//        sessionReq.setTrainerId(2L);
//
//        // Stubbing
//        Class targetClass = new Class();
//        targetClass.setSessions(new HashSet<>());
//        when(classRepository.findById(classId)).thenReturn(Optional.of(targetClass));
//
//        // Method call
//        List<SessionRes> updatedSessions = classServiceImpl.updateAllSessionsInClass(classId, sessionReq);
//
//        // Verification
//        verify(classRepository, times(1)).findById(classId);
//        verify(sessionRepository, times(sessionReq.getListOfSessionDate().size())).save(any(Session.class));
//
//    }
//
//
//
//    @Test
//    public void testUpdateAllSessionsInClass_ClassNotFound() {
//        // Mock data
//        Long classId = 1L;
//        SessionReq sessionReq = new SessionReq();
//        sessionReq.setStart("09:00");
//        sessionReq.setEnd("11:00");
//        sessionReq.setListOfSessionDate(Collections.singletonList(LocalDate.now()));
//        sessionReq.setFsu("F-Town 2");
//        sessionReq.setAdminId(1L);
//        sessionReq.setTrainerId(2L);
//
//        Class targetClass = new Class();
//        targetClass.setSessions(new HashSet<>());
//        when(classRepository.findById(classId)).thenReturn(Optional.empty());
//        when(classReposi)
//
//        // Method call
//        ApiException exception = assertThrows(ApiException.class, () -> {
//            classServiceImpl.updateAllSessionsInClass(classId, sessionReq);
//        });
//
//        // Verification
//        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
//        assertEquals("Class not found with id: " + classId, exception.getMessage());
//    }
    @Test
    void testMorningPeriodValidTimes() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        assertDoesNotThrow(() -> classServiceImpl.isStartTimeAndEndTimeValid(startTime, endTime));
    }

    @Test
    void testMorningPeriodInvalidEndTime() {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(12, 2); // This is outside the morning period
        assertThrows(ApiException.class, () -> classServiceImpl.isStartTimeAndEndTimeValid(startTime, endTime));
    }

    @Test
    void testNoonPeriodValidTimes() {
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        assertDoesNotThrow(() -> classServiceImpl.isStartTimeAndEndTimeValid(startTime, endTime));
    }

    @Test
    void testNoonPeriodInvalidEndTime() {
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(17, 2); // This is outside the noon period
        assertThrows(ApiException.class, () -> classServiceImpl.isStartTimeAndEndTimeValid(startTime, endTime));
    }

    @Test
    void testNightPeriodValidTimes() {
        LocalTime startTime = LocalTime.of(18, 0);
        LocalTime endTime = LocalTime.of(21, 0);
        assertDoesNotThrow(() -> classServiceImpl.isStartTimeAndEndTimeValid(startTime, endTime));
    }

    @Test
    void testNightPeriodInvalidEndTime() {
        LocalTime startTime = LocalTime.of(18, 0);
        LocalTime endTime = LocalTime.of(22, 2); // This is outside the night period
        assertThrows(ApiException.class, () -> classServiceImpl.isStartTimeAndEndTimeValid(startTime, endTime));
    }

    @Test
    void testStartTimeOutsideDefinedPeriods() {
        LocalTime startTime = LocalTime.of(7, 30); // Before any defined period
        LocalTime endTime = LocalTime.of(8, 30);
        assertThrows(ApiException.class, () -> classServiceImpl.isStartTimeAndEndTimeValid(startTime, endTime));
    }

    @Test
    void shouldUpdateScheduledClassesToOpening() {
        LocalDate now = LocalDate.now();
        Class scheduledClass = new Class(); // Assume Class is your entity class
        scheduledClass.setStatus("Scheduled");
        List<Class> scheduledClasses = Arrays.asList(scheduledClass);

        when(classRepository.findScheduledClassesBefore(any(LocalDate.class)))
                .thenReturn(scheduledClasses);

        classServiceImpl.updateClassStatuses();

        verify(classRepository, times(1)).findScheduledClassesBefore(now);
        verify(classRepository, times(1)).save(scheduledClass);
        assertEquals("Opening", scheduledClass.getStatus());
    }

    @Test
    void shouldUpdateOpeningClassesToClosed() {
        LocalDate now = LocalDate.now();
        Class openingClass = new Class(); // Assume Class is your entity class
        openingClass.setStatus("Opening");
        List<Class> openingClasses = Arrays.asList(openingClass);

        when(classRepository.findOpeningClassesAfter(any(LocalDate.class)))
                .thenReturn(openingClasses);

        classServiceImpl.updateClassStatuses();

        verify(classRepository, times(1)).findOpeningClassesAfter(now);
        verify(classRepository, times(1)).save(openingClass);
        assertEquals("Closed", openingClass.getStatus());
    }

    @Test
    void testBothDatesProvided() {
        // Setup test data and mock behavior
        Page<Class> classesPage = new PageImpl<>(Collections.emptyList());
        when(classRepository.findFilteredClassesWhenFromDateAndToDateNotNull(
                any(), anyList(), anyList(), any(LocalDate.class), any(LocalDate.class), anyList(), anyList(), anyList(), any(PageRequest.class)))
                .thenReturn(classesPage);
        when(classConverter.convertToPageRes(any())).thenCallRealMethod(); // Adjust this to match your actual method

        // Execute the method
        Page<ClassDTO> result = classServiceImpl.findFilteredClasses(null, Collections.emptyList(), Collections.emptyList(), LocalDate.now(), LocalDate.now().plusDays(1),
                Collections.emptyList(),  Collections.emptyList(), Collections.emptyList(), 0, 10);

        assertNotNull(result);
        // Verify interactions
        verify(classRepository, times(1)).findFilteredClassesWhenFromDateAndToDateNotNull(
                any(), anyList(), anyList(), any(LocalDate.class), any(LocalDate.class), anyList(), anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testDatesBothNull() {
        // Setup test data and mock behavior
        Page<Class> classesPage = new PageImpl<>(Collections.emptyList());
        when(classRepository.findFilteredClassesWhenFromDateAndToDateNull(
                any(), anyList(), anyList(), anyList(), anyList(), anyList(), any(PageRequest.class)))
                .thenReturn(classesPage);
        when(classConverter.convertToPageRes(any())).thenCallRealMethod(); // Adjust this to match your actual method

        // Execute the method
        Page<ClassDTO> result =  classServiceImpl.findFilteredClasses(null, Collections.emptyList(), Collections.emptyList(), null, null,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 0, 10);

        assertNotNull(result);
        // Verify interactions
        verify(classRepository, times(1)).findFilteredClassesWhenFromDateAndToDateNull(
                any(), anyList(), anyList(), anyList(), anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void convertValidSessionToSessionRes() {
        // Setup
        Session session = new Session();
        session.setId(1L);
        // Assuming Class is a valid entity related to Session
        Class aClass = new Class();
        aClass.setCode("Code123");
        aClass.setName("Class Name");
        session.setAClass(aClass);
        session.setDayProgress(5);
        session.setTotalDays(30);
        session.setLocation("Location");
        session.setClassTime("Morning");
        session.setTrainerName("Trainer");
        session.setAdminName("Admin");
        session.setStartTime(LocalTime.of(8,0));
        session.setEndTime(LocalTime.of(12,0));
        session.setSessionDate(LocalDate.now());

        // Execution
        SessionRes sessionRes = classServiceImpl.convertToSessionRes(session);

        // Validation
        assertNotNull(sessionRes);
        assertEquals(session.getId(), sessionRes.getSessionId());
        assertEquals(session.getAClass().getCode(), sessionRes.getClassCode());
        assertEquals(session.getAClass().getName(), sessionRes.getClassName());
        assertEquals(session.getDayProgress(), sessionRes.getDayProgress());
        assertEquals(session.getTotalDays(), sessionRes.getTotalDays());
        assertEquals(session.getLocation(), sessionRes.getLocation());
        assertEquals(session.getClassTime(), sessionRes.getClassTime());
        assertEquals(session.getTrainerName(), sessionRes.getTrainerName());
        assertEquals(session.getAdminName(), sessionRes.getAdminName());
        assertEquals(session.getStartTime(), sessionRes.getStart());
        assertEquals(session.getEndTime(), sessionRes.getEnd());
        assertEquals(session.getSessionDate(), sessionRes.getSessionDate());
    }

    @Test
    void convertNullSessionToSessionRes() {
        // Execution
        SessionRes sessionRes = classServiceImpl.convertToSessionRes(null);

        // Validation
        assertNotNull(sessionRes);
        assertNull(sessionRes.getClassId()); // Assuming default values are null
    }

    @Test
    void convertSessionWithNullFieldsToSessionRes() {
        // Setup
        Session session = new Session(); // No fields set

        // Execution
        SessionRes sessionRes = classServiceImpl.convertToSessionRes(session);

        // Validation
        assertNotNull(sessionRes);
        assertNull(sessionRes.getClassId()); // Assuming default values are null
        assertNull(sessionRes.getClassCode()); // Validate a few key fields
        assertNull(sessionRes.getClassName());
    }

    @Test
    public void testMorningClassTime() {
        String result = classServiceImpl.setClassTimeBaseOnStartTime(LocalTime.of(8, 0));
        assertEquals("Morning", result);
    }

    @Test
    public void testNoonClassTime() {
        String result = classServiceImpl.setClassTimeBaseOnStartTime(LocalTime.of(13, 0));
        assertEquals("Noon", result);
    }

    @Test
    public void testNightClassTime() {
        String result = classServiceImpl.setClassTimeBaseOnStartTime(LocalTime.of(18, 0));
        assertEquals("Night", result);
    }

    @Test
    public void testInvalidStartTime() {
        ApiException exception = assertThrows(ApiException.class, () -> classServiceImpl.setClassTimeBaseOnStartTime(LocalTime.of(7, 0)));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Start time is not in any particular period", exception.getMessage());
    }

}
