package com.example.phase1_fams.converter;

import com.example.phase1_fams.dto.LearningMaterialDto;
import com.example.phase1_fams.dto.TrainingProgramDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.*;
import com.example.phase1_fams.model.LearningObjective;
import com.example.phase1_fams.model.Syllabus;
import com.example.phase1_fams.model.TrainingProgram;
import com.example.phase1_fams.model.TrainingProgramSyllabus;
import com.example.phase1_fams.service.LearningMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TrainingProgramConverter {

    private final LearningMaterialService learningMaterialService;

    @Autowired
    public TrainingProgramConverter(LearningMaterialService learningMaterialService) {
        this.learningMaterialService = learningMaterialService;
    }

    public TrainingProgramDTO convertToPageRes(TrainingProgram trainingProgram){
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setId(trainingProgram.getId());
        trainingProgramDTO.setTrainingProgramName(trainingProgram.getName());
        trainingProgramDTO.setDuration(trainingProgram.getDuration());
        trainingProgramDTO.setStatus(trainingProgram.getStatus());
        trainingProgramDTO.setCreatedBy(trainingProgram.getCreatedBy());
        trainingProgramDTO.setCreatedDate(trainingProgram.getCreatedDate());
        trainingProgramDTO.setModifiedBy(trainingProgram.getModifiedBy());
        trainingProgramDTO.setModifiedDate(trainingProgram.getModifiedDate());
        return trainingProgramDTO;
    }

    public TrainingProgramRes convertToTrainingProgramRes(TrainingProgram trainingProgram){
        TrainingProgramRes trainingProgramRes = new TrainingProgramRes();
        try {
            trainingProgramRes.setId(trainingProgram.getId());
            trainingProgramRes.setTrainingProgramName(trainingProgram.getName());
            trainingProgramRes.setGeneralInformation(trainingProgram.getDescription());
            trainingProgramRes.setDuration(trainingProgram.getDuration());
            trainingProgramRes.setStatus(trainingProgram.getStatus());
            trainingProgramRes.setCreatedBy(trainingProgram.getCreatedBy());
            trainingProgramRes.setCreatedDate(trainingProgram.getCreatedDate());
            trainingProgramRes.setModifiedBy(trainingProgram.getModifiedBy());
            trainingProgramRes.setModifiedDate(trainingProgram.getModifiedDate());
            List<SyllabusContent> syllabusContents = new ArrayList<>();
            for (TrainingProgramSyllabus tps : trainingProgram.getTrainingProgramSyllabusSet()) {
                if (tps.getTrainingProgram().getId().equals(trainingProgram.getId())) {
                    Syllabus syllabus = tps.getSyllabus();
                    int sequence = tps.getSequence();
                    List<DaysUnitRes> daysUnitResList = syllabus.getDaysUnits().stream()
                            .map(dayUnit -> {
                                List<TrainingUnitRes> trainingUnitResList = dayUnit.getTrainingUnits().stream()
                                        .map(trainingUnit -> {
                                            List<TrainingContentRes> trainingContentResList = trainingUnit.getTrainingContents().stream()
                                                    .map(trainingContent -> {
                                                        List<String> objCodes = trainingContent.getObjectiveCodes().stream()
                                                                .map(LearningObjective::getCode).toList();
                                                        List<LearningMaterialDto> learningMaterialDtoList = trainingContent.getLearningMaterials().stream()
                                                                .map(learningMaterial -> new LearningMaterialDto(learningMaterial.getId(), learningMaterial.getFileName(),
                                                                        learningMaterial.getFileType(), learningMaterialService.generateUrl(learningMaterial.getFileName() + "." + learningMaterial.getFileType(), HttpMethod.GET, trainingContent.getId()),
                                                                        learningMaterial.getUploadBy(), learningMaterial.getUploadDate()))
                                                                .toList();
                                                        return new TrainingContentRes(trainingContent.getId(), trainingContent.getOrderNumber(),
                                                                trainingContent.getName(), trainingContent.getDuration(), objCodes,
                                                                trainingContent.getDeliveryType(), trainingContent.getMethod(), learningMaterialDtoList);
                                                    }).sorted(Comparator.comparingInt(TrainingContentRes::getOrderNumber)).collect(Collectors.toList());

                                            return new TrainingUnitRes(trainingUnit.getId(), trainingUnit.getUnitNumber(),
                                                    trainingUnit.getUnitName(), trainingUnit.getTrainingTime(), trainingContentResList);

                                        }).sorted(Comparator.comparingInt(TrainingUnitRes::getUnitNumber)).collect(Collectors.toList());

                                return new DaysUnitRes(dayUnit.getId(), dayUnit.getDayNumber(), trainingUnitResList);
                            }).sorted(Comparator.comparingInt(DaysUnitRes::getDayNumber)).collect(Collectors.toList());

                    int totalDays = syllabus.getDaysUnits().size();
                    SyllabusContent syllabusContent = new SyllabusContent(sequence, syllabus.getName(), syllabus.getCode(),
                            syllabus.getVersion(), syllabus.getTotalTime(), totalDays, syllabus.getStatus(), daysUnitResList,
                            syllabus.getCreatedDate(), syllabus.getCreatedBy(), syllabus.getModifiedDate(), syllabus.getModifiedBy());
                    syllabusContents.add(syllabusContent);
                }
            }
            trainingProgramRes.setSyllabusContents(syllabusContents);
            trainingProgramRes.getSyllabusContents().sort(Comparator.comparingInt(SyllabusContent::getSequence));
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error mapping training program details!");
        }
        return trainingProgramRes;
    }
}
