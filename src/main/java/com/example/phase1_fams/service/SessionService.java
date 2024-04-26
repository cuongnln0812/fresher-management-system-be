package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.dto.response.SessionRes;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface SessionService {
    List<SessionRes> getSessionListByDate(LocalDate startDate, LocalDate endDate);

    List<SessionRes> getOrFilterSession(
            String keywords,
            List<String> locations,
            List<String> classTime,
            LocalDate startDate,
            LocalDate endDate,
            List<String> statuses,
            List<String> fsus,
            List<String> trainer
    );

}
