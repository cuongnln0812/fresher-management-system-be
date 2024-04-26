package com.example.phase1_fams.converter;

import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.ClassDetailsRes;
import com.example.phase1_fams.dto.response.TrainingProgramRes;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.model.Class;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClassConverterTests {
    @Mock
    TrainingProgramConverter trainingProgramConverter;

    @InjectMocks
    ClassConverter classConverter;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToPageRes_Success(){
        Class aClass = new Class();
        aClass.setName("Java");
        aClass.setCode("JAVA101");

        ClassDTO result = classConverter.convertToPageRes(aClass);

        assertEquals(result.getCode(), aClass.getCode());
        assertEquals(result.getName(), aClass.getName());
    }

    @Test
    public void testConvertToDetailsRes_Success() {
        // Creating a mock Class object
        Class aClass = new Class();
        aClass.setId(1L);
        aClass.setCode("EC001");
        aClass.setName("Example Class");
        aClass.setStatus("Active");
        ClassUser adminUserType = new ClassUser();
        Users adminUser = new Users();
        adminUser.setId(1L);
        adminUser.setName("Admin User");
        adminUserType.setUserType("Class Admin");
        adminUserType.setUsers(adminUser);
        adminUserType.setId(new ClassUserKey(adminUser.getId(), aClass.getId()));
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(adminUserType);
        aClass.setClassUserType(classUserSet);
        Session session = new Session();
        session.setId(1L);
        session.setSessionDate(LocalDate.now());
        Set<Session> sessions = new HashSet<>();
        aClass.setSessions(sessions);
        TrainingProgram trainingProgram = new TrainingProgram();// Assuming existence
        aClass.setTrainingProgram(trainingProgram);
        TrainingProgramRes trainingProgramRes = new TrainingProgramRes(); // Simplify creation
        when(trainingProgramConverter.convertToTrainingProgramRes(trainingProgram)).thenReturn(trainingProgramRes);

        // Execute the method under test
        ClassDetailsRes result = classConverter.convertToDetailsRes(aClass);

        // Assertions to verify the expected outcomes
        assertNotNull(result);
        assertEquals("EC001", result.getClassCode());
        assertEquals("Example Class", result.getClassName());
        assertEquals("Active", result.getStatus());
        assertEquals("Admin User", result.getClassGeneralDTO().getAdmin());
        assertEquals(1L, result.getClassGeneralDTO().getAdminId());

        // Verify the trainingProgramConverter interaction
        verify(trainingProgramConverter).convertToTrainingProgramRes(trainingProgram);
    }

    @Test
    public void testConvertToDetailsRes_SessionDateIsNull() {
        Class aClass = new Class();
        aClass.setId(1L);
        aClass.setName("Example Class");
        aClass.setStatus("Active");
        Session session = new Session();
        session.setId(1L);
        Set<Session> sessions = new HashSet<>();
        aClass.setSessions(sessions);

        assertThrows(ApiException.class, () -> classConverter.convertToDetailsRes(aClass));    }
}