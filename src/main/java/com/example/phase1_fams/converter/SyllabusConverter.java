package com.example.phase1_fams.converter;

import com.example.phase1_fams.dto.LearningMaterialDto;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.*;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.*;
import com.example.phase1_fams.service.LearningMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SyllabusConverter {

    private final SyllabusRepository syllabusRepository;
    private final DaysUnitRepository daysUnitRepository;
    private final TrainingUnitRepository trainingUnitRepository;
    private final TrainingContentRepository trainingContentRepository;
    private final LearningMaterialRepository learningMaterialRepository;
    private final LearningObjectiveRepository learningObjectiveRepository;
    private final LearningMaterialService learningMaterialService;

    @Autowired
    public SyllabusConverter(SyllabusRepository syllabusRepository, DaysUnitRepository daysUnitRepository, TrainingUnitRepository trainingUnitRepository, TrainingContentRepository trainingContentRepository, LearningMaterialRepository learningMaterialRepository, LearningObjectiveRepository learningObjectiveRepository, LearningMaterialService learningMaterialService) {
        this.syllabusRepository = syllabusRepository;
        this.daysUnitRepository = daysUnitRepository;
        this.trainingUnitRepository = trainingUnitRepository;
        this.trainingContentRepository = trainingContentRepository;
        this.learningMaterialRepository = learningMaterialRepository;
        this.learningObjectiveRepository = learningObjectiveRepository;
        this.learningMaterialService = learningMaterialService;
    }

    public SyllabusDetailsRes convertToDetailsRes(Syllabus newSyllabus){
        SyllabusDetailsRes syllabusRes = new SyllabusDetailsRes();
        // Update the details
        float totalTimes = 0.0F;
        syllabusRes.setCode(newSyllabus.getCode());
        syllabusRes.setSyllabusName(newSyllabus.getName());
        syllabusRes.setVersion(newSyllabus.getVersion());
        syllabusRes.setDuration(newSyllabus.getDaysUnits().size());
        syllabusRes.setTotalTimes(newSyllabus.getTotalTime());
        syllabusRes.getSyllabusGeneral().setLevel(newSyllabus.getLevel());
        syllabusRes.getSyllabusGeneral().setCourseObjectives(newSyllabus.getCourseObjectives());
        syllabusRes.getSyllabusGeneral().setAttendeeNumber(newSyllabus.getAttendeeNumber());
        syllabusRes.getSyllabusGeneral().setTotalOutputStandards(syllabusRepository.findDistinctObjectiveCodes(newSyllabus.getCode()));
        syllabusRes.getSyllabusGeneral().setTechnicalRequirements(newSyllabus.getTechnicalRequirements());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setQuiz(newSyllabus.getQuizAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().set_final(newSyllabus.getFinalAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setGpa(newSyllabus.getGpaCriteria());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setAssignment(newSyllabus.getAssignmentAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setFinalTheory(newSyllabus.getFinalTheoryAssessment());
        syllabusRes.getSyllabusOthers().getAssessmentScheme().setFinalPractice(newSyllabus.getFinalPracticeAssessment());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setTraining(newSyllabus.getTrainingPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setRetest(newSyllabus.getReTestPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setOthers(newSyllabus.getOthersPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setMarking(newSyllabus.getMarkingPrinciple());
        syllabusRes.getSyllabusOthers().getTrainingDeliveryPrinciple().setWaiverCriteria(newSyllabus.getWaiverCriteriaPrinciple());
        syllabusRes.setCreatedBy(newSyllabus.getCreatedBy());
        syllabusRes.setCreatedDate(newSyllabus.getCreatedDate());
        syllabusRes.setModifiedBy(newSyllabus.getModifiedBy());
        syllabusRes.setModifiedDate(newSyllabus.getModifiedDate());
        syllabusRes.setStatus(newSyllabus.getStatus());
        for (DaysUnit daysUnit: newSyllabus.getDaysUnits()) {
            if(daysUnit.getId() != null) {
                DaysUnitRes newDay = getDayUnit(daysUnit);
                syllabusRes.getSyllabusOutline().addDayUnit(newDay);
            }
        }
        syllabusRes.getSyllabusOutline().getDays().sort(Comparator.comparingInt(DaysUnitRes::getDayNumber));
        return syllabusRes;
    }

    private DaysUnitRes getDayUnit(DaysUnit dayUnitDTO) {
        DaysUnitRes daysUnitRes = new DaysUnitRes();
        DaysUnit daysUnit = daysUnitRepository.findById(dayUnitDTO.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Day not found to map to response!"));

        daysUnitRes.setId(dayUnitDTO.getId());
        daysUnitRes.setDayNumber(dayUnitDTO.getDayNumber());
        for (TrainingUnit trainingUnitDTO : dayUnitDTO.getTrainingUnits()) {
            if(trainingUnitDTO.getId() != null) {
                TrainingUnitRes trainingUnit = getTrainingUnit(trainingUnitDTO);
                daysUnitRes.addTraining(trainingUnit);
            }
        }
        daysUnitRes.getTrainingUnits().sort(Comparator.comparingInt(TrainingUnitRes::getUnitNumber));
        return daysUnitRes;
    }

    private TrainingUnitRes getTrainingUnit(TrainingUnit trainingUnitDTO) {
        TrainingUnitRes trainingUnitRes = new TrainingUnitRes();
        trainingUnitRepository.findById(trainingUnitDTO.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training Unit not found to map to response!!"));
        // Set properties for trainingUnit from trainingUnitDTO
        trainingUnitRes.setId(trainingUnitDTO.getId());
        trainingUnitRes.setUnitNumber(trainingUnitDTO.getUnitNumber());
        trainingUnitRes.setUnitName(trainingUnitDTO.getUnitName());
        trainingUnitRes.setTrainingTime(trainingUnitDTO.getTrainingTime());
        for (TrainingContent trainingContentDTO:
                trainingUnitDTO.getTrainingContents()) {
            if(trainingContentDTO.getId() != null) {
                TrainingContentRes trainingContent = getTrainingContent(trainingContentDTO);
                trainingUnitRes.addContents(trainingContent);
            }
        }
        trainingUnitRes.getTrainingContents().sort(Comparator.comparingInt(TrainingContentRes::getOrderNumber));
        return trainingUnitRes;
    }

    private TrainingContentRes getTrainingContent(TrainingContent trainingContentDTO) {
        TrainingContentRes trainingContentRes = new TrainingContentRes();
        trainingContentRepository.findById(trainingContentDTO.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training Content not found to map to response!!!"));
        trainingContentRes.setId(trainingContentDTO.getId());
        trainingContentRes.setOrderNumber(trainingContentDTO.getOrderNumber());
        trainingContentRes.setContentName(trainingContentDTO.getName());
        trainingContentRes.setDuration(trainingContentDTO.getDuration());
        trainingContentRes.setDeliveryType(trainingContentDTO.getDeliveryType());
        trainingContentRes.setMethod(trainingContentDTO.getMethod());
        List<String> objectiveSet = new ArrayList<>();

        for (LearningObjective objective : trainingContentDTO.getObjectiveCodes()) {
            String objectiveCode = objective.getCode();
            LearningObjective obj = learningObjectiveRepository.findByCode(objectiveCode)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Output standard not found to map to response"));
            // Add the LearningObjective to the set
            objectiveSet.add(objectiveCode);
        }
        trainingContentRes.setOutputStandards(objectiveSet);
        for(LearningMaterial lm: trainingContentDTO.getLearningMaterials()){
            LearningMaterialDto lmDto = getLearningMaterial(lm, trainingContentDTO.getId());
            trainingContentRes.addMaterials(lmDto);
        }
        return trainingContentRes;
    }

    private LearningMaterialDto getLearningMaterial(LearningMaterial lm, Long trainingContentId) {
        LearningMaterialDto lmDto = new LearningMaterialDto();
        learningMaterialRepository.findById(lm.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Learning material not found to map to response!!"));
        // Set properties for trainingUnit from trainingUnitDTO
        lmDto.setFileId(lm.getId());
        lmDto.setFileName(lm.getFileName());
        lmDto.setFileType(lm.getFileType());
        lmDto.setDownloadURL(learningMaterialService.generateUrl(lm.getFileName() + "." + lm.getFileType(), HttpMethod.GET, trainingContentId));
        lmDto.setUploadBy(lm.getUploadBy());
        lmDto.setUploadDate(lm.getUploadDate());
        return lmDto;
    }

    public ActiveSyllabus convertToActiveList(Syllabus syllabus){
        ActiveSyllabus syllabusPageRes = new ActiveSyllabus();
        syllabusPageRes.setCode(syllabus.getCode());
        syllabusPageRes.setName(syllabus.getName());
        syllabusPageRes.setCreatedDate(syllabus.getCreatedDate());
        syllabusPageRes.setCreatedBy(syllabus.getCreatedBy());
        syllabusPageRes.setVersion(syllabus.getVersion());
        syllabusPageRes.setDuration(syllabus.getDaysUnits().size());
        syllabusPageRes.setTotalTime(syllabus.getTotalTime());
        syllabusPageRes.setStatus(syllabus.getStatus());
        return syllabusPageRes;
    }


    public SyllabusPageRes convertToPageRes(Syllabus syllabus){
        SyllabusPageRes syllabusPageRes = new SyllabusPageRes();
        syllabusPageRes.setCode(syllabus.getCode());
        syllabusPageRes.setSyllabusName(syllabus.getName());
        syllabusPageRes.setCreatedDate(syllabus.getCreatedDate());
        syllabusPageRes.setCreatedBy(syllabus.getCreatedBy());
        syllabusPageRes.setOutputStandard(syllabusRepository.findDistinctObjectiveCodes(syllabus.getCode()));
        syllabusPageRes.setDuration(syllabus.getDaysUnits().size());
        syllabusPageRes.setStatus(syllabus.getStatus());
        return syllabusPageRes;
    }
}
