package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.response.MaterialRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface LearningMaterialService {
    MaterialRes saveFiles(List<MultipartFile> file, List<Long> deletedMaterialIds, Long trainingContentId);

    String generateUrl(String fileName, HttpMethod get, Long trainingContentId);
}