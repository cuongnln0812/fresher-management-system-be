package com.example.phase1_fams.service;

import com.example.phase1_fams.converter.SessionConverter;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.SessionRes;
import com.example.phase1_fams.model.Class;
import com.example.phase1_fams.model.Session;
import com.example.phase1_fams.repository.SessionRepository;
import com.example.phase1_fams.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionServiceTests {
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionConverter sessionConverter;
    @InjectMocks
    private SessionServiceImpl sessionService;

    Session testSession1;
    Session testSession2;
    Class testClass;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init(){
        testClass = Class.builder()
                .code("HCM22_CPL_JAVA02")
                .name("Java Web Developer Internship")
                .build();
        testSession1 = Session.builder()
                .id(1L)
                .dayProgress(1)
                .sessionDate(LocalDate.of(2024, Calendar.FEBRUARY, 4))
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(10,0))
                .location("FTown1")
                .trainerName("Trainer")
                .adminName("Admin")
                .aClass(testClass)
                .build();
        testSession2 = Session.builder()
                .id(2L)
                .dayProgress(1)
                .sessionDate(LocalDate.of(2024, Calendar.FEBRUARY, 7))
                .startTime(LocalTime.of(9,0))
                .endTime(LocalTime.of(11,0))
                .location("FTown2")
                .trainerName("Trainer")
                .adminName("Admin")
                .aClass(testClass)
                .build();
    }

    @Test
    void getSessionListByDate_GetASpecificDateSuccess(){
        LocalDate startDate = LocalDate.of(2024, 2, 4);
//        Date endDate = LocalDate.of(2024, Calendar.FEBRUARY, 8);
//        Date endDate = null;
        List<Session> sessionList = new ArrayList<>();
        sessionList.add(testSession1);
        //Arrange
        when(sessionRepository.findBySessionDate(startDate)).thenReturn(sessionList);
        when(sessionConverter.convertToSessionResList(sessionList)).thenAnswer(invocation -> {
            List<Session> test = invocation.getArgument(0);
            return mapToResList(test);
        });

        List<SessionRes> result = sessionService.getSessionListByDate(startDate, null);

        verify(sessionRepository, times(1)).findBySessionDate(startDate);
        verify(sessionConverter, times(1)).convertToSessionResList(sessionList);
        assertEqualsSession(sessionList, result);
    }

    @Test
    void getSessionListByDate_GetDateBetweenSuccess(){
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 8);
//        Date endDate = null;
        List<Session> sessionList = new ArrayList<>();
        sessionList.add(testSession1);
        sessionList.add(testSession2);
        //Arrange
        when(sessionRepository.findBySessionDateBetweenOrderBySessionDateAsc(startDate, endDate)).thenReturn(sessionList);
        when(sessionConverter.convertToSessionResList(sessionList)).thenAnswer(invocation -> {
            List<Session> test = invocation.getArgument(0);
            return mapToResList(test);
        });

        List<SessionRes> result = sessionService.getSessionListByDate(startDate, endDate);

        verify(sessionRepository, times(1)).findBySessionDateBetweenOrderBySessionDateAsc(startDate,endDate);
        verify(sessionConverter, times(1)).convertToSessionResList(sessionList);
        assertEqualsSession(sessionList, result);
    }

    @Test
    void getSessionListByDate_InvalidDateFound(){
        LocalDate startDate = null;
        LocalDate endDate = null;
//        Date endDate = null;
        List<Session> sessionList = new ArrayList<>();
        sessionList.add(testSession1);
        sessionList.add(testSession2);

        verify(sessionRepository, times(0)).findBySessionDateBetweenOrderBySessionDateAsc(startDate,endDate);
        verify(sessionRepository, times(0)).findBySessionDate(startDate);
        verify(sessionConverter, times(0)).convertToSessionResList(sessionList);
        ApiException exception = assertThrows(ApiException.class, () -> sessionService.getSessionListByDate(startDate, endDate),
                "Expected getSessionListByDate to throw invalid date, but it didn't");

        assertEquals("Invalid Start date or End date!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getSessionListByDate_StartDateMustBeforeEndDate(){
        LocalDate endDate = LocalDate.of(2024, 2, 1);
        LocalDate startDate = LocalDate.of(2024, 2, 8);
//        Date endDate = null;
        List<Session> sessionList = new ArrayList<>();
        sessionList.add(testSession1);
        sessionList.add(testSession2);

        verify(sessionRepository, times(0)).findBySessionDateBetweenOrderBySessionDateAsc(startDate,endDate);
        verify(sessionRepository, times(0)).findBySessionDate(startDate);
        verify(sessionConverter, times(0)).convertToSessionResList(sessionList);
        ApiException exception = assertThrows(ApiException.class, () -> sessionService.getSessionListByDate(startDate, endDate),
                "Expected getSessionListByDate to throw exception for starDate must before, but it didn't");

        assertEquals("Start date cannot after End date", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getSessionListByDate_StartDateMustNotNull(){
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2024, 2, 8);
        List<Session> sessionList = new ArrayList<>();
        sessionList.add(testSession1);
        sessionList.add(testSession2);

        verify(sessionRepository, times(0)).findBySessionDateBetweenOrderBySessionDateAsc(startDate,endDate);
        verify(sessionRepository, times(0)).findBySessionDate(startDate);
        verify(sessionConverter, times(0)).convertToSessionResList(sessionList);
        ApiException exception = assertThrows(ApiException.class, () -> sessionService.getSessionListByDate(startDate, endDate),
                "Expected getSessionListByDate to throw exception for starDate must not null, but it didn't");

        assertEquals("Invalid Start date or End date!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getOrFilterSessionTest() {
        // Mock input parameters
        String keywords = "example";
        List<String> locations = Arrays.asList("Location1", "Location2");
        List<String> classTime = Arrays.asList("Morning", "Evening");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        List<String> statuses = Arrays.asList("Status1", "Status2");
        List<String> fsus = Arrays.asList("FSU1", "FSU2");
        List<String> admin = Arrays.asList("Admin1", "Admin2");
        List<String> trainer = Arrays.asList("Trainer1", "Trainer2");

        // Mocked returned session list from repository
        List<Session> mockSessions = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findFilteredSession(keywords, locations, classTime, startDate, endDate, statuses, fsus, trainer))
                .thenReturn(mockSessions);

        // Mocked conversion to SessionRes list
        List<SessionRes> mockSessionResList = Arrays.asList(new SessionRes(), new SessionRes());
        when(sessionConverter.convertToSessionResList(mockSessions)).thenReturn(mockSessionResList);

        // Execute the method under test
        List<SessionRes> result = sessionService.getOrFilterSession(keywords, locations, classTime, startDate, endDate, statuses, fsus, trainer);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify interactions
        verify(sessionRepository).findFilteredSession(keywords, locations, classTime, startDate, endDate, statuses, fsus, trainer);
        verify(sessionConverter).convertToSessionResList(mockSessions);
    }

    public List<SessionRes> mapToResList(List<Session> test){
        List<SessionRes> resList = new ArrayList<>();
        for (Session session:
                test) {
            SessionRes res = new SessionRes();
            res.setSessionId(session.getId());
            res.setClassCode(session.getAClass().getCode());
            res.setClassName(session.getAClass().getName());
            res.setDayProgress(session.getDayProgress());
            res.setTotalDays(session.getTotalDays());
            res.setLocation(session.getLocation());
            res.setTrainerName(session.getTrainerName());
            res.setAdminName(session.getAdminName());
            res.setSessionDate(session.getSessionDate());
            res.setStart(session.getStartTime());
            res.setEnd(session.getEndTime());
            resList.add(res);
        }
        return resList;
    }
    void assertEqualsSession(List<Session> sessionList, List<SessionRes> result){
        assertEquals(sessionList.size(), result.size(), "The size of the result list should match the expected list.");
        for (int i = 0; i < sessionList.size(); i++) {
            Session expectedSession = sessionList.get(i);
            SessionRes actualSessionRes = result.get(i);
            assertEquals(expectedSession.getId(), actualSessionRes.getSessionId(), "ID should match");
            assertEquals(expectedSession.getAClass().getCode(), actualSessionRes.getClassCode(), "Class code should match");
            assertEquals(expectedSession.getAClass().getName(), actualSessionRes.getClassName(), "Class name should match");
            assertEquals(expectedSession.getDayProgress(), actualSessionRes.getDayProgress(), "Day progress should match");
            assertEquals(expectedSession.getLocation(), actualSessionRes.getLocation(), "Location should match");
            assertEquals(expectedSession.getTrainerName(), actualSessionRes.getTrainerName(), "Trainer name should match");
            assertEquals(expectedSession.getAdminName(), actualSessionRes.getAdminName(), "Admin name should match");
            assertEquals(expectedSession.getSessionDate(), actualSessionRes.getSessionDate(), "Session date should match");
            assertEquals(expectedSession.getStartTime(), actualSessionRes.getStart(), "Start time should match");
            assertEquals(expectedSession.getEndTime(), actualSessionRes.getEnd(), "End time should match");
        }
    }
}
