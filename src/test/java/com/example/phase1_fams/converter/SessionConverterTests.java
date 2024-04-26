package com.example.phase1_fams.converter;

import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.SessionRes;
import com.example.phase1_fams.model.Class;
import com.example.phase1_fams.model.Session;
import com.example.phase1_fams.service.ClassService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SessionConverterTests {
    @InjectMocks
    SessionConverter sessionConverter;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToSessionResList() {
        // Create a list of Session objects
        List<Session> sessions = new ArrayList<>();
        Session session1 = new Session();
        Class aClass = new Class();
        aClass.setCode("C001");
        aClass.setName("Java Programming");
        session1.setId(1L);
        sessions.add(session1);
        session1.setAClass(aClass);


        // Call
        List<SessionRes> resList = sessionConverter.convertToSessionResList(sessions);

        // Assertions
        assertEquals(1, resList.size());
        assertEquals(1, resList.get(0).getSessionId());
        assertEquals("C001", resList.get(0).getClassCode());
        assertEquals("Java Programming", resList.get(0).getClassName());
        // Add assertions for other properties of SessionRes objects
    }

    @Test
    void testConvertToSessionResListWithNullClass() {
        // Create a list of Session objects with one session having null class
        List<Session> sessions = new ArrayList<>();
        Session session = new Session();
        // Set properties of session
        sessions.add(session);

        // Verify that an ApiException is thrown
        assertThrows(ApiException.class, () -> sessionConverter.convertToSessionResList(sessions));
    }
}