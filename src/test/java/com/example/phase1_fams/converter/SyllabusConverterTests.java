package com.example.phase1_fams.converter;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.dto.response.ActiveSyllabus;
import com.example.phase1_fams.dto.response.SyllabusDetailsRes;
import com.example.phase1_fams.dto.response.SyllabusPageRes;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.*;
import com.example.phase1_fams.service.LearningMaterialService;
import com.example.phase1_fams.service.impl.SyllabusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class SyllabusConverterTests {
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
    private LearningMaterialService learningMaterialService;
    @InjectMocks
    private SyllabusConverter syllabusConverter;

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

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init(){
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

        daysUnitSet.add(testDayUnit);
        trainingUnitSet.add(testTrainingUnit);
        trainingContentSet.add(testTrainingContent);
        learningObjectiveSet.add(testLearningObjective1);
        learningObjectiveSet.add(testLearningObjective2);
    }

    @Test
    void testConvertToDetailRes_Success(){
        when(syllabusRepository.findDistinctObjectiveCodes(testSyllabus.getCode())).thenReturn(List.of(testLearningObjective1.getCode(),
                                                                                                       testLearningObjective2.getCode()));
        when(daysUnitRepository.findById(any())).thenReturn(Optional.of(testDayUnit));
        when(trainingUnitRepository.findById(any())).thenReturn(Optional.of(testTrainingUnit));
        when(trainingContentRepository.findById(any())).thenReturn(Optional.of(testTrainingContent));
        when(learningObjectiveRepository.findByCode(any())).thenReturn(Optional.of(testLearningObjective1))
                                                           .thenReturn(Optional.of(testLearningObjective2));
        when(learningMaterialService.generateUrl(anyString(), eq(HttpMethod.GET), anyLong())).thenReturn("urlLink");

        // Call the method
        SyllabusDetailsRes result = syllabusConverter.convertToDetailsRes(testSyllabus);

        assertNotNull(result);
    }

    @Test
    void testConvertToActiveSyllabus_Success(){
        ActiveSyllabus result = syllabusConverter.convertToActiveList(testSyllabus);
        assertNotNull(result);
    }

    @Test
    void testConvertToPageRes_Success(){
        when(syllabusRepository.findDistinctObjectiveCodes(testSyllabus.getCode())).thenReturn(new ArrayList<>());

        SyllabusPageRes result = syllabusConverter.convertToPageRes(testSyllabus);
        assertNotNull(result);
    }
}
