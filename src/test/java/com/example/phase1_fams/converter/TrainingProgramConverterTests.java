package com.example.phase1_fams.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.example.phase1_fams.dto.TrainingProgramDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.SyllabusContent;
import com.example.phase1_fams.dto.response.TrainingProgramRes;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.DaysUnitRepository;
import com.example.phase1_fams.service.LearningMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class TrainingProgramConverterTests {

    @Mock
    private LearningMaterialService learningMaterialService;
    @InjectMocks
    private TrainingProgramConverter trainingProgramConverter;

    TrainingProgram testExistingTrainingProgram;

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


    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init(){


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
    public void testConvertToPageRes_Success() {

        // Test the method
        TrainingProgramDTO actualDTO = trainingProgramConverter.convertToPageRes(testExistingTrainingProgram);

        // Verify the result
        assertEquals(testExistingTrainingProgram.getId(), actualDTO.getId());
        assertEquals(testExistingTrainingProgram.getName(), actualDTO.getTrainingProgramName());
        assertEquals(testExistingTrainingProgram.getDuration(), actualDTO.getDuration());
        assertEquals(testExistingTrainingProgram.getModifiedBy(), actualDTO.getModifiedBy());
        assertEquals(testExistingTrainingProgram.getModifiedDate(), actualDTO.getModifiedDate());
        assertEquals(testExistingTrainingProgram.getCreatedDate(), actualDTO.getCreatedDate());
        assertEquals(testExistingTrainingProgram.getCreatedBy(), actualDTO.getCreatedBy());
        assertEquals(testExistingTrainingProgram.getStatus(), actualDTO.getStatus());
    }

    @Test
    public void testConvertToTrainingProgramRes_Success() {
        when(learningMaterialService.generateUrl(anyString(), eq(HttpMethod.GET), anyLong())).thenReturn("urlLink");

        // Test the method
        TrainingProgramRes result = trainingProgramConverter.convertToTrainingProgramRes(testExistingTrainingProgram);

        // Verify the result
        assertEquals(result.getId(), testExistingTrainingProgram.getId());
        assertEquals(result.getTrainingProgramName(), testExistingTrainingProgram.getName());
        assertEquals(result.getGeneralInformation(), testExistingTrainingProgram.getDescription());
        assertEquals(result.getDuration(), testExistingTrainingProgram.getDuration());
        assertEquals(result.getStatus(), testExistingTrainingProgram.getStatus());
        assertEquals(result.getCreatedBy(), testExistingTrainingProgram.getCreatedBy());
        assertEquals(result.getCreatedDate(), testExistingTrainingProgram.getCreatedDate());
        assertEquals(result.getModifiedBy(), testExistingTrainingProgram.getModifiedBy());
        assertEquals(result.getModifiedDate(), testExistingTrainingProgram.getModifiedDate());
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
    public void testConvertToTrainingProgramRes_ThrowException() {

        // Prepare test data
        TrainingProgram trainingProgram = new TrainingProgram();
        // Set other properties as needed

        // Mock dependencies

        // Set other properties as needed
        ApiException exception = assertThrows(ApiException.class, () -> {
            trainingProgramConverter.convertToTrainingProgramRes(trainingProgram);
        });

        // Verify the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals("Error mapping training program details!", exception.getMessage());
    }
}