package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.dto.request.ClassReq;
import com.example.phase1_fams.dto.request.ClassReqUpdate;
import com.example.phase1_fams.dto.request.SessionReq;
import com.example.phase1_fams.dto.response.ClassDetailsRes;
import com.example.phase1_fams.dto.response.ClassRes;
import com.example.phase1_fams.dto.response.SessionRes;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ClassService {
    void deactivateClass(Long classId);
    Page<ClassDTO> findFilteredClasses(
            String keywords,
            List<String> locations,
            List<String> attendeeTypes,
            LocalDate fromDates,
            LocalDate toDates,
            List<String> times,
            List<String> statuses,
            List<String> fsus,
            int page,
            int size
    );

//    SessionRes updateSessionInClass(Long sessionId, SessionReq sessionReq);

    List<SessionRes> updateAllSessionsInClass(Long classId, SessionReq sessionReq);

    ClassRes createClassAsScheduled(ClassReq classReq);

    ClassRes createClassAsPlanning(ClassReq classReq);

    ClassDetailsRes getClassDetails(Long classId);

    ClassRes updateClassAsScheduled(Long classId, ClassReqUpdate updatedClassReq);

    ClassRes updateClassAsPlanning(Long classId, ClassReqUpdate updatedClassReq);

}
