package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.ClassDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.phase1_fams.dto.TrainingProgramDTO;
import com.example.phase1_fams.dto.request.TrainingProgramReq;
import com.example.phase1_fams.dto.request.TrainingProgramReqUpdate;
import com.example.phase1_fams.dto.response.TrainingProgramRes;

import java.time.LocalDate;
import java.util.List;

@Service
public interface TrainingProgramService {
    TrainingProgramRes getTrainingProgramDetails(Long id);

    void importTrainingProgram(MultipartFile file, Integer duplicateOption);

    void importTrainingProgramCSV(MultipartFile file, Integer duplicateOption);

    TrainingProgramRes duplicateTrainingProgram(Long id);

    TrainingProgramRes switchStatus(Long id);

    InputStreamResource downloadFileFromGoogleDrive(String googleDriveLink);

    TrainingProgramRes createTrainingProgramAsActive(TrainingProgramReq trainingProgramReq);

    TrainingProgramRes createTrainingProgramAsDraft(TrainingProgramReq trainingProgramReq);

    TrainingProgramRes updateTrainingProgramAsActive(TrainingProgramReqUpdate trainingProgramReqUpdate);

    TrainingProgramRes updateTrainingProgramAsDraft(TrainingProgramReqUpdate trainingProgramReqUpdate);

    Page<TrainingProgramDTO> searchTrainingPrograms(String keyword, List<String> createdBy, LocalDate startDate, LocalDate endDate,
                                                           Integer duration, List<Integer> statuses, Pageable pageable);

    List<TrainingProgramDTO> getActiveTrainingProgramList(String name);

    Page<ClassDTO> getClassListByTrainingProgram(Long id, int page, int size);
}