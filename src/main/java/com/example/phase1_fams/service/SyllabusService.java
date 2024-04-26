package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.response.ActiveSyllabus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.phase1_fams.dto.request.SyllabusReq;
import com.example.phase1_fams.dto.request.SyllabusReqUpdate;
import com.example.phase1_fams.dto.response.SyllabusDetailsRes;
import com.example.phase1_fams.dto.response.SyllabusPageRes;
import com.example.phase1_fams.model.Syllabus;

import java.time.LocalDate;
import java.util.List;

@Service
public interface SyllabusService {

    void processImportedFile2(MultipartFile file, Integer duplicateOption);

    InputStreamResource downloadFileFromGoogleDrive(String googleDriveLink);

    SyllabusDetailsRes getSyllabusDetails(String code);

    SyllabusDetailsRes createSyllabusAsActive(SyllabusReq syllabus);

    SyllabusDetailsRes createSyllabusAsDraft(SyllabusReq syllabus);

    SyllabusDetailsRes updateSyllabusAsActive(SyllabusReqUpdate syllabusDetails);

    SyllabusDetailsRes updateSyllabusAsDraft(SyllabusReqUpdate syllabusDetails);

    SyllabusDetailsRes duplicateSyllabus(String code);

    SyllabusDetailsRes deactiveSyllabus(String code);

    Page<SyllabusPageRes> searchSyllabus(String searchKey, List<String> createdBy, LocalDate startDate, LocalDate endDate,
                                         Integer duration, List<String> outputStandards, List<Integer> statuses,  int page, int size);

    List<ActiveSyllabus> getActiveSyllabusList(String name);
}
