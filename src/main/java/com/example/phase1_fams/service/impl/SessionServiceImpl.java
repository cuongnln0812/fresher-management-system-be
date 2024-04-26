package com.example.phase1_fams.service.impl;

import com.example.phase1_fams.converter.SessionConverter;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.SessionRes;
import com.example.phase1_fams.model.Session;
import com.example.phase1_fams.repository.SessionRepository;
import com.example.phase1_fams.service.SessionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionConverter sessionConverter;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository, SessionConverter sessionConverter) {
        this.sessionRepository = sessionRepository;
        this.sessionConverter = sessionConverter;
    }

    @Override
    public List<SessionRes> getSessionListByDate(LocalDate startDate, LocalDate endDate) {
        //Không cần trường hợp startDate null vì bên controller bắt buộc không được null
        if(endDate == null && startDate != null){
            List<Session> sessions = sessionRepository.findBySessionDate(startDate);
            return sessionConverter.convertToSessionResList(sessions);
        }else if(endDate != null && startDate != null) {
            if (startDate.isAfter(endDate))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Start date cannot after End date");

            List<Session> sessions = sessionRepository.findBySessionDateBetweenOrderBySessionDateAsc(startDate, endDate);
            return sessionConverter.convertToSessionResList(sessions);
        }else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Start date or End date!");
        }
    }

    @Override
    public List<SessionRes> getOrFilterSession(String keywords, List<String> locations, List<String> classTime,
                                               LocalDate startDate, LocalDate endDate, List<String> statuses, List<String> fsus,
                                               List<String> trainer) {
        List<Session> sessions = sessionRepository.findFilteredSession(keywords, locations, classTime, startDate, endDate, statuses, fsus, trainer);
        return sessionConverter.convertToSessionResList(sessions);
    }
}
