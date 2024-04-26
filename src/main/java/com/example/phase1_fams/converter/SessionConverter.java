package com.example.phase1_fams.converter;

import java.util.ArrayList;
import java.util.List;

import com.example.phase1_fams.dto.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.phase1_fams.dto.response.SessionRes;
import com.example.phase1_fams.model.Session;

@Component
public class SessionConverter {

    public List<SessionRes> convertToSessionResList(List<Session> sessions) {
        List<SessionRes> resList = new ArrayList<>();
        for (Session session : sessions) {
            if(session.getAClass() == null) throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Class of session id: " + session.getId() + " is null");
            SessionRes res = new SessionRes();
            res.setSessionId(session.getId());
            res.setClassId(session.getAClass().getId());
            res.setClassCode(session.getAClass().getCode());
            res.setClassName(session.getAClass().getName());
            res.setClassStatus(session.getAClass().getStatus());
            res.setDayProgress(session.getDayProgress());
            res.setTotalDays(session.getTotalDays());
            res.setClassTime(session.getAClass().getClassTime());
            res.setLocation(session.getLocation());
            res.setFsu(session.getAClass().getFsu());
            res.setTrainerName(session.getTrainerName());
            res.setAdminName(session.getAdminName());
            res.setSessionDate(session.getSessionDate());
            res.setStart(session.getStartTime());
            res.setEnd(session.getEndTime());
            resList.add(res);
        }
        return resList;
    }
}
