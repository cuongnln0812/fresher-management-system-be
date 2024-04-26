package com.example.phase1_fams.service.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.example.phase1_fams.dto.response.*;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.SyllabusConverter;
import com.example.phase1_fams.dto.DaysUnitDTO;
import com.example.phase1_fams.dto.SyllabusOthersDTO;
import com.example.phase1_fams.dto.TrainingContentDTO;
import com.example.phase1_fams.dto.TrainingUnitDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.exception.ResourceNotFoundException;
import com.example.phase1_fams.dto.request.SyllabusReq;
import com.example.phase1_fams.dto.request.SyllabusReqUpdate;
import com.example.phase1_fams.service.SyllabusService;

@Service
@Transactional
public class SyllabusServiceImpl implements SyllabusService {

    private final SyllabusRepository syllabusRepository;
    private final LearningMaterialRepository learningMaterialRepository;
    private final LearningObjectiveRepository learningObjectiveRepository;
    private final TrainingContentRepository trainingContentRepository;
    private final TrainingUnitRepository trainingUnitRepository;
    private final DaysUnitRepository daysUnitRepository;
    private final AuthenticationService authenticationService;
    private final SyllabusConverter syllabusConverter;
    private final ModelMapper modelMapper;
    private final TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;
    private static final Logger logger = LoggerFactory.getLogger(SyllabusServiceImpl.class);

    @Autowired
    public SyllabusServiceImpl(SyllabusRepository syllabusRepository,
                               LearningMaterialRepository learningMaterialRepository, LearningObjectiveRepository learningObjectiveRepository,
                               TrainingContentRepository trainingContentRepository,
                               TrainingUnitRepository trainingUnitRepository,
                               DaysUnitRepository daysUnitRepository,
                               AuthenticationService authenticationService,
                               SyllabusConverter syllabusConverter,
                               ModelMapper modelMapper, TrainingProgramSyllabusRepository trainingProgramSyllabusRepository) {
        this.syllabusRepository = syllabusRepository;
        this.learningMaterialRepository = learningMaterialRepository;
        this.learningObjectiveRepository = learningObjectiveRepository;
        this.trainingContentRepository = trainingContentRepository;
        this.trainingUnitRepository = trainingUnitRepository;
        this.daysUnitRepository = daysUnitRepository;
        this.syllabusConverter = syllabusConverter;
        this.authenticationService = authenticationService;
        this.modelMapper = modelMapper;
        this.trainingProgramSyllabusRepository = trainingProgramSyllabusRepository;
    }

    @Override
    public InputStreamResource downloadFileFromGoogleDrive(String googleDriveLink) {
        try {
            // Extract file ID from the Google Drive link
            String fileId = googleDriveLink.substring(googleDriveLink.lastIndexOf("=") + 1);

            // Construct the direct download link using the file ID
            String directDownloadLink = "https://drive.google.com/uc?export=download&id=" + fileId;

            // Open a connection to the direct download link
            URL url = new URL(directDownloadLink);
            URLConnection connection = url.openConnection();

            // Set user-agent header to mimic a web browser
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Return the input stream resource
            return new InputStreamResource(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void processImportedFile2(MultipartFile file, Integer duplicateOption) {
        String syllabusCode = null;
        boolean skipTrainingDataImport = false;
        validateImportedFile(file);
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

            // Assuming there are two sheets in the workbook
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                XSSFSheet sheet = workbook.getSheetAt(sheetIndex);

                if (sheet.getSheetName().equals("SyllabusData")) {
                    DataFormatter formatter = new DataFormatter();
                    Row row = sheet.getRow(1); // Get the second row

                    Syllabus syllabus = new Syllabus();
                    syllabus.setCode(formatter.formatCellValue(row.getCell(0)));
                    syllabus.setName(formatter.formatCellValue(row.getCell(1)));
                    String versionString = formatter.formatCellValue(row.getCell(2));
                    int version = Integer.parseInt(versionString);
                    syllabus.setVersion(version);
                    syllabus.setStatus(2);
                    syllabus.setCourseObjectives(formatter.formatCellValue(row.getCell(3)));
                    syllabus.setLevel(formatter.formatCellValue(row.getCell(4)));
                    syllabus.setTechnicalRequirements(formatter.formatCellValue(row.getCell(5)));
                    String quizAssessmentString = formatter.formatCellValue(row.getCell(6));
                    int quizAssessment = Integer.parseInt(quizAssessmentString);
                    syllabus.setQuizAssessment(quizAssessment);
                    String assignmentAssessmentString = formatter.formatCellValue(row.getCell(7));
                    int assignmentAssessment = Integer.parseInt(assignmentAssessmentString);
                    syllabus.setAssignmentAssessment(assignmentAssessment);
                    String finalPracticeAssessmentString = formatter.formatCellValue(row.getCell(8));
                    int finalPracticeAssessment = Integer.parseInt(finalPracticeAssessmentString);
                    syllabus.setFinalPracticeAssessment(finalPracticeAssessment);
                    String finalExamAssessmentString = formatter.formatCellValue(row.getCell(9));
                    int finalExamAssessment = Integer.parseInt(finalExamAssessmentString);
                    syllabus.setFinalAssessment(finalExamAssessment);
                    String finalTheoryAssessmentString = formatter.formatCellValue(row.getCell(10));
                    int finalTheoryAssessment = Integer.parseInt(finalTheoryAssessmentString);
                    syllabus.setFinalTheoryAssessment(finalTheoryAssessment);
                    String gpaAssessmentString = formatter.formatCellValue(row.getCell(11));
                    int gpaAssessment = Integer.parseInt(gpaAssessmentString);
                    syllabus.setGpaCriteria(gpaAssessment);
                    syllabus.setTrainingPrinciple(formatter.formatCellValue(row.getCell(12)));
                    syllabus.setReTestPrinciple(formatter.formatCellValue(row.getCell(13)));
                    syllabus.setMarkingPrinciple(formatter.formatCellValue(row.getCell(14)));
                    syllabus.setWaiverCriteriaPrinciple(formatter.formatCellValue(row.getCell(15)));
                    syllabus.setOthersPrinciple(formatter.formatCellValue(row.getCell(16)));
                    syllabus.setCreatedBy(authenticationService.getName());
                    syllabus.setCreatedDate();

                    if (!isValidAssessment(syllabus.getQuizAssessment())
                            || !isValidAssessment(syllabus.getAssignmentAssessment())
                            || !isValidAssessment(syllabus.getFinalTheoryAssessment())
                            || !isValidAssessment(syllabus.getFinalPracticeAssessment())
                            || !isValidAssessment(syllabus.getGpaCriteria())) {
                        throw new ApiException(HttpStatus.BAD_REQUEST,
                                "Assessment should larger than 0 and smaller than 100");
                    }
                    int finalAssesment = syllabus.getFinalPracticeAssessment()
                            + syllabus.getFinalTheoryAssessment();
                    int totalAssesment = syllabus.getQuizAssessment()
                            + syllabus.getAssignmentAssessment()
                            + syllabus.getFinalAssessment();
                    if (finalAssesment != 100)
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Final Assessment is not equal 100!");
                    if (totalAssesment != 100)
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Total Assessment is not equal 100!");

                    if (isCodeExisting(syllabus.getCode())) {
                        switch (duplicateOption) {
                            case 1:
                                int duplicateCounter = 0;
                                String orginalCode = syllabus.getCode();

                                while (isCodeExisting(syllabus.getCode())) {
                                    duplicateCounter++;
                                    syllabus.setCode(orginalCode + "_" + duplicateCounter);
                                }
                                syllabusRepository.save(syllabus);
                                break;
                            case 2:
                                syllabusRepository.findByCode(syllabus.getCode())
                                        .ifPresent(existingSyllabus -> {
                                            // Delete all training program syllabuses associated with the existing
                                            // syllabus
                                            trainingProgramSyllabusRepository
                                                    .deleteBySyllabusCode(existingSyllabus.getCode());

                                            // Delete the existing syllabus
                                            syllabusRepository.delete(existingSyllabus);

                                            // Save the new syllabus
                                            syllabusRepository.save(syllabus);
                                        });
                                break;
                            case 0:
                                skipTrainingDataImport = true;
                                break;
                            default:
                                throw new ApiException(HttpStatus.BAD_REQUEST,
                                        "Duplicate option must be 1 or 2 or 0 (allow, replace, skip)");
                        }
                    } else {
                        // Code is unique, save the new Syllabus
                        syllabusRepository.save(syllabus);
                    }
                    syllabusCode = syllabus.getCode();
                } else if (sheet.getSheetName().equals("TrainingData")) {
                    DataFormatter formatter = new DataFormatter();
                    Syllabus syllabus = syllabusRepository.findByCode(syllabusCode)
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Syllabus not found"));
                    // Read the headers into a list
                    Row headerRow = sheet.getRow(0);
                    List<String> headers = new ArrayList<>();
                    for (Cell cell : headerRow) {
                        headers.add(formatter.formatCellValue(cell));
                    }
                    DaysUnit lastDay = null;
                    TrainingUnit lastTrainingUnit = null;
                    String lastUnitNumber = null;
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        Row row = sheet.getRow(i);
                        DaysUnit dayUnit = null;
                        TrainingUnit trainingUnit = null;
                        TrainingContent trainingContent = null;
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j);
                            String cellValue = formatter.formatCellValue(cell);
                            switch (headers.get(j)) {
                                case "Day":
                                    if (!cellValue.isEmpty()) {
                                        dayUnit = new DaysUnit();
                                        dayUnit.setDayNumber(Integer.parseInt(cellValue));
                                        dayUnit.setSyllabus(syllabus);
                                        syllabus.getDaysUnits().add(dayUnit);
                                        if (!skipTrainingDataImport == true) {
                                            daysUnitRepository.save(dayUnit);
                                        }
                                        lastDay = dayUnit; // Update the last created day
                                    } else if (lastDay != null) {
                                        // If day is empty, use the last day
                                        dayUnit = lastDay;
                                    }
                                    break;
                                case "Unit Number":
                                    if (!cellValue.isEmpty()) {
                                        trainingUnit = new TrainingUnit();
                                        trainingUnit.setUnitNumber(Integer.parseInt(cellValue));

                                        trainingUnit.setDaysUnit(dayUnit);
                                        if (!skipTrainingDataImport == true) {
                                            trainingUnitRepository.save(trainingUnit);
                                        }
                                        logger.info("Saved TrainingUnit with unit_code: " + cellValue);
                                        lastUnitNumber = cellValue;
                                        lastTrainingUnit = trainingUnit;
                                    }
                                    break;
                                case "Unit Name":
                                    if (trainingUnit != null) {
                                        trainingUnit.setUnitName(cellValue);
                                    }
                                    break;
                                case "Content Number":
                                    if (!cellValue.isEmpty()) {
                                        trainingContent = new TrainingContent();
                                        trainingContent.setOrderNumber(Integer.parseInt(cellValue));
                                        if (dayUnit == null && lastDay != null) {
                                            dayUnit = lastDay;
                                        }
                                        if (trainingUnit == null && lastTrainingUnit != null) {
                                            trainingUnit = lastTrainingUnit;
                                        }

                                        if (dayUnit != null && trainingUnit != null) {
                                            trainingContent.setTrainingUnit(trainingUnit);
                                            if (!skipTrainingDataImport == true) {
                                                trainingContentRepository.save(trainingContent);
                                            }
                                            logger.info("Saved TrainingContent with content_number: " + cellValue);
                                        }
                                    }
                                    break;
                                case "Content Name":
                                    if (trainingContent != null) {
                                        trainingContent.setName(cellValue);
                                    }
                                    break;
                                case "Output Standards":
                                    if (trainingContent != null) {
                                        Set<LearningObjective> objectiveCodes = Arrays.stream(cellValue.split(", "))
                                                .map(objectiveCode -> learningObjectiveRepository
                                                        .findByCode(objectiveCode)
                                                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                                                "LearningObjective not found for code: "
                                                                        + objectiveCode)))
                                                .collect(Collectors.toSet());

                                        trainingContent.setObjectiveCodes(objectiveCodes);
                                    }

                                    break;
                                case "Duration (minutes)":
                                    if (trainingContent != null && cellValue != null && !cellValue.isEmpty()) {
                                        trainingContent.setDuration(Integer.parseInt(cellValue));
                                    }
                                    break;
                                case "Delivery Type":
                                    if (trainingContent != null) {
                                        trainingContent.setDeliveryType(cellValue);
                                    }
                                    break;
                                case "Method":
                                    if (trainingContent != null) {
                                        trainingContent.setMethod(cellValue);
                                    }
                                    break;
                            }
                        }
                        if (dayUnit != null) {
                            dayUnit.addTrainingUnit(trainingUnit);
                        }

                        if (trainingUnit != null && trainingContent != null) {
                            trainingUnit.addTrainingContent(trainingContent);
                            float duration = trainingContent.getDuration();
                            if(trainingUnit.getTrainingTime() == null) trainingUnit.setTrainingTime(0.0F);

                            float trainingTime = trainingUnit.getTrainingTime() + (duration / 60);
                            trainingUnit.setTrainingTime(trainingTime);

                            if (!skipTrainingDataImport) {
                                trainingUnitRepository.save(trainingUnit);
                            }
                        }

                        if (dayUnit != null) {
                            if (!skipTrainingDataImport == true) {
                                daysUnitRepository.save(dayUnit);
                            }
                        }
                    }
                    AtomicReference<Float> totalTimes = new AtomicReference<>(0.0F);

                    syllabus.getDaysUnits()
                            .forEach(daysUnit -> {
                                daysUnit.getTrainingUnits().forEach(trainingUnit -> {
                                    totalTimes.updateAndGet(v -> v + trainingUnit.getTrainingTime()); // Safely accumulate training times
                                });
                            });

                    syllabus.setTotalTime(totalTimes.get()); // Set the accumulated value
                } else
                    throw new ApiException(HttpStatus.BAD_REQUEST, "SheetName is wrong!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SyllabusDetailsRes getSyllabusDetails(String code) {
        Syllabus syllabus = syllabusRepository.findByCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Syllabus not found!!"));
        return syllabusConverter.convertToDetailsRes(syllabus);
    }

    @Override
    public SyllabusDetailsRes createSyllabusAsActive(SyllabusReq syllabusReq) {
        validatingSyllabusOthersField(syllabusReq.getSyllabusOthers());
        Syllabus newSyllabus = createSyllabus(syllabusReq);
        newSyllabus.setVersion(1);
        newSyllabus.setStatus(1);
        Syllabus createdSyllabus = syllabusRepository.save(newSyllabus);
        return syllabusConverter.convertToDetailsRes(createdSyllabus);
    }

    @Override
    public SyllabusDetailsRes createSyllabusAsDraft(SyllabusReq syllabusReq) {
        Syllabus newSyllabus = createSyllabus(syllabusReq);
        newSyllabus.updateVersionDraft();
        newSyllabus.setVersion(0);
        newSyllabus.setStatus(2);
        Syllabus createdSyllabus = syllabusRepository.save(newSyllabus);
        return syllabusConverter.convertToDetailsRes(createdSyllabus);
    }

    public void validatingSyllabusOthersField(SyllabusOthersDTO syllabusReq) {
        if (!isValidAssessment(syllabusReq.getAssessmentScheme().getQuiz())
                || !isValidAssessment(syllabusReq.getAssessmentScheme().getAssignment())
                || !isValidAssessment(syllabusReq.getAssessmentScheme().getFinalTheory())
                || !isValidAssessment(syllabusReq.getAssessmentScheme().getFinalPractice())
                || !isValidAssessment(syllabusReq.getAssessmentScheme().getGpa())
                || !isValidAssessment(syllabusReq.getAssessmentScheme().get_final())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Assessment should larger than 0 and smaller than 100");
        }
        int finalAssesment = syllabusReq.getAssessmentScheme().getFinalPractice()
                + syllabusReq.getAssessmentScheme().getFinalTheory();
        int totalAssesment = syllabusReq.getAssessmentScheme().getQuiz()
                + syllabusReq.getAssessmentScheme().getAssignment()
                + syllabusReq.getAssessmentScheme().get_final();
        if (finalAssesment != 100)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Final Assessment is not equal 100!");
        if (totalAssesment != 100)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Total Assessment is not equal 100!");
    }

    public Syllabus createSyllabus(SyllabusReq syllabusReq) {
        Float totalTimes = 0.0F;
        if (isCodeExisting(syllabusReq.getCode())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Syllabus code is already in use");
        }
        Syllabus newSyllabus = new Syllabus();
        newSyllabus.setCode(syllabusReq.getCode());
        newSyllabus.setName(syllabusReq.getSyllabusName());
        newSyllabus.setAttendeeNumber(syllabusReq.getSyllabusGeneral().getAttendeeNumber());
        newSyllabus.setLevel(syllabusReq.getSyllabusGeneral().getLevel());
        newSyllabus.setTechnicalRequirements(syllabusReq.getSyllabusGeneral().getTechnicalRequirements());
        newSyllabus.setCourseObjectives(syllabusReq.getSyllabusGeneral().getCourseObjectives());
        newSyllabus.setQuizAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getQuiz());
        newSyllabus.setAssignmentAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getAssignment());
        newSyllabus.setFinalAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().get_final());
        newSyllabus
                .setFinalPracticeAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalPractice());
        newSyllabus.setFinalTheoryAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalTheory());
        newSyllabus.setGpaCriteria(syllabusReq.getSyllabusOthers().getAssessmentScheme().getGpa());
        newSyllabus.setTrainingPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getTraining());
        newSyllabus.setMarkingPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getMarking());
        newSyllabus.setOthersPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getOthers());
        newSyllabus.setReTestPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getRetest());
        newSyllabus.setWaiverCriteriaPrinciple(
                syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getWaiverCriteria());
        newSyllabus.setCreatedBy(authenticationService.getName());
        newSyllabus.setCreatedDate();
        newSyllabus = syllabusRepository.save(newSyllabus);
        for (DaysUnitDTO daysUnitDTO : syllabusReq.getSyllabusOutline().getDays()) {
            DaysUnit newDay = createDayUnit(daysUnitDTO);
            newSyllabus.addDayUnit(newDay);
            for (TrainingUnit trainingUnit : newDay.getTrainingUnits()) {
                totalTimes += trainingUnit.getTrainingTime();
            }
        }
        newSyllabus.setTotalTime(totalTimes);
        return newSyllabus;
    }

    private DaysUnit createDayUnit(DaysUnitDTO dayUnitDTO) {
        DaysUnit dayUnit = new DaysUnit();
        // Set properties for dayUnit from dayUnitDTO
        dayUnit.setDayNumber(dayUnitDTO.getDayNumber());
        dayUnit = daysUnitRepository.save(dayUnit);
        for (TrainingUnitDTO trainingUnitDTO : dayUnitDTO.getTrainingUnits()) {
            TrainingUnit trainingUnit = createTrainingUnit(trainingUnitDTO);
            dayUnit.addTrainingUnit(trainingUnit);
        }
        return daysUnitRepository.save(dayUnit);
    }

    private TrainingUnit createTrainingUnit(TrainingUnitDTO trainingUnitDTO) {
        TrainingUnit trainingUnit = new TrainingUnit();
        // Set properties for trainingUnit from trainingUnitDTO
        trainingUnit.setUnitNumber(trainingUnitDTO.getUnitNumber());
        trainingUnit.setUnitName(trainingUnitDTO.getUnitName());
        float totalDuration = 0.0F;
        trainingUnit = trainingUnitRepository.save(trainingUnit);
        for (TrainingContentDTO trainingContentDTO : trainingUnitDTO.getTrainingContents()) {
            TrainingContent trainingContent = createTrainingContent(trainingContentDTO);
            trainingUnit.addTrainingContent(trainingContent);
            totalDuration += trainingContent.getDuration();
        }
        totalDuration = totalDuration / 60;
        trainingUnit.setTrainingTime(totalDuration);
        return trainingUnitRepository.save(trainingUnit);
    }

    private TrainingContent createTrainingContent(TrainingContentDTO trainingContentDTO) {
        TrainingContent trainingContent = new TrainingContent();
        trainingContent.setOrderNumber(trainingContentDTO.getOrderNumber());
        trainingContent.setName(trainingContentDTO.getContentName());
        trainingContent.setDuration(trainingContentDTO.getDuration());
        trainingContent.setDeliveryType(trainingContentDTO.getDeliveryType());
        trainingContent.setMethod(trainingContentDTO.getMethod());
        Set<LearningObjective> objectiveSet = new HashSet<>();
        for (String objCode : trainingContentDTO.getOutputStandards()) {
            LearningObjective objective = learningObjectiveRepository.findByCode(objCode)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Output standard not found to create"));
            // Add the LearningObjective to the set
            objectiveSet.add(objective);
        }
        trainingContent.setObjectiveCodes(objectiveSet);
        trainingContent.setLearningMaterials(new HashSet<>());
        return trainingContentRepository.save(trainingContent);
    }

    public boolean isCodeExisting(String code) {
        Syllabus existingCode = syllabusRepository.findByCode(code)
                .orElse(null);
        return existingCode != null;
    }

    private boolean isValidAssessment(int value) {
        // Add any additional validation logic here
        return value >= 0 && value <= 100;
    }

    public void validateImportedFile(MultipartFile file) {
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid file format. Please upload a Excel file.");
        }

    }

    @Override
    public SyllabusDetailsRes updateSyllabusAsActive(SyllabusReqUpdate syllabusReq) {
        Syllabus updatedSyllabus = syllabusRepository.findByCode(syllabusReq.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus", "code", syllabusReq.getCode()));
        validatingSyllabusOthersField(syllabusReq.getSyllabusOthers());
        // Update the details
        updatedSyllabus = updateSyllabus(syllabusReq, updatedSyllabus);
        if (updatedSyllabus.getI1() != 0) {
            if (syllabusReq.getVersion() == updatedSyllabus.getI1())
                updatedSyllabus.updateVersionActive();
            else
                updatedSyllabus.setVersion(syllabusReq.getVersion());
        } else {
            updatedSyllabus.setVersion(1);
        }
        updatedSyllabus.setStatus(1);
        Syllabus syllabus = syllabusRepository.save(updatedSyllabus);
        return syllabusConverter.convertToDetailsRes(syllabus);
    }

    @Override
    public SyllabusDetailsRes updateSyllabusAsDraft(SyllabusReqUpdate syllabusReq) {
        Syllabus updatedSyllabus = syllabusRepository.findByCode(syllabusReq.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus", "code", syllabusReq.getCode()));
        if(isSyllabusInUsed(updatedSyllabus)) throw new ApiException(HttpStatus.CONFLICT,
                "You cannot save as draft for this syllabus as it is in used in 1 or more Training Program!");
        // Update the details
        updatedSyllabus = updateSyllabus(syllabusReq, updatedSyllabus);
        if (syllabusReq.getVersion() == updatedSyllabus.getI1())
            updatedSyllabus.updateVersionDraft();
        else
            updatedSyllabus.setVersion(syllabusReq.getVersion());
        updatedSyllabus.setStatus(2);
        Syllabus syllabus = syllabusRepository.save(updatedSyllabus);
        return syllabusConverter.convertToDetailsRes(syllabus);
    }

    public Syllabus updateSyllabus(SyllabusReqUpdate syllabusReq, Syllabus newSyllabus) {
        Float totalTimes = 0.0F;
        newSyllabus.setName(syllabusReq.getSyllabusName());
        newSyllabus.setAttendeeNumber(syllabusReq.getSyllabusGeneral().getAttendeeNumber());
        newSyllabus.setLevel(syllabusReq.getSyllabusGeneral().getLevel());
        newSyllabus.setTechnicalRequirements(syllabusReq.getSyllabusGeneral().getTechnicalRequirements());
        newSyllabus.setCourseObjectives(syllabusReq.getSyllabusGeneral().getCourseObjectives());
        newSyllabus.setQuizAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getQuiz());
        newSyllabus.setAssignmentAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getAssignment());
        newSyllabus.setFinalAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().get_final());
        newSyllabus.setFinalPracticeAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalPractice());
        newSyllabus.setFinalTheoryAssessment(syllabusReq.getSyllabusOthers().getAssessmentScheme().getFinalTheory());
        newSyllabus.setGpaCriteria(syllabusReq.getSyllabusOthers().getAssessmentScheme().getGpa());
        newSyllabus.setTrainingPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getTraining());
        newSyllabus.setMarkingPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getMarking());
        newSyllabus.setOthersPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getOthers());
        newSyllabus.setReTestPrinciple(syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getRetest());
        newSyllabus.setWaiverCriteriaPrinciple(
                syllabusReq.getSyllabusOthers().getTrainingDeliveryPrinciple().getWaiverCriteria());
        newSyllabus.setModifiedBy(authenticationService.getName());
        newSyllabus.setModifiedDate();

        try {
            if(!syllabusReq.getDeletedDaysId().isEmpty()) {
                for (Long daysUnitId : syllabusReq.getDeletedDaysId()) {
                    daysUnitRepository.findById(daysUnitId).ifPresent(daysUnit -> {
                        newSyllabus.getDaysUnits().remove(daysUnit);
                        daysUnitRepository.delete(daysUnit);
                    });
                }
            }
            if (!syllabusReq.getDeletedTrainingUnitsId().isEmpty()) {
                for (Long trainingUnitId : syllabusReq.getDeletedTrainingUnitsId()) {
                    trainingUnitRepository.findById(trainingUnitId).ifPresent(trainingUnit -> {
                        newSyllabus.getDaysUnits().forEach(daysUnit -> {
                            daysUnit.getTrainingUnits().removeIf(tu -> tu.getId().equals(trainingUnit.getId()));
                        });
                        trainingUnitRepository.delete(trainingUnit);
                    });
                }
            }
            if(!syllabusReq.getDeletedTrainingContentsId().isEmpty()) {
                for (Long trainingContentId : syllabusReq.getDeletedTrainingContentsId()) {
                    trainingContentRepository.findById(trainingContentId).ifPresent(trainingContent -> {
                        // Assuming TrainingContent has a getLearningMaterials method
                        List<LearningMaterial> learningMaterials = new ArrayList<>(trainingContent.getLearningMaterials());
                        // Remove LearningMaterials from the TrainingContent
                        trainingContent.getLearningMaterials().clear();
                        // Now iterate over the copied list of learning materials to delete them from the repository
                        learningMaterialRepository.deleteAll(learningMaterials);

                        newSyllabus.getDaysUnits().forEach(daysUnit -> {
                            daysUnit.getTrainingUnits().forEach(trainingUnit -> {
                                trainingUnit.getTrainingContents().removeIf(tc -> tc.getId().equals(trainingContent.getId()));
                            });
                        });
                        trainingContentRepository.delete(trainingContent);
                    });
                }
            }
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delting resources of syllabus");
        }

        for (DaysUnitRes daysUnitDTO : syllabusReq.getSyllabusOutline().getDays()) {
            DaysUnit daysUnit;
            if (daysUnitDTO.getId() == null) {
                DaysUnitDTO dto = modelMapper.map(daysUnitDTO, DaysUnitDTO.class);
                daysUnit = createDayUnit(dto);
            } else {
                daysUnit = updateDayUnit(daysUnitDTO.getId(), daysUnitDTO);
            }
            newSyllabus.addDayUnit(daysUnit);
            for (TrainingUnit trainingUnit : daysUnit.getTrainingUnits()) {
                totalTimes += trainingUnit.getTrainingTime();
            }
        }
        newSyllabus.setTotalTime(totalTimes);
        return newSyllabus;
    }

    private DaysUnit updateDayUnit(Long id, DaysUnitRes dayUnitDTO) {
        DaysUnit dayUnit = daysUnitRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Day with id: " + id + " not found to update!"));
        // Set properties for dayUnit from dayUnitDTO
        dayUnit.setDayNumber(dayUnitDTO.getDayNumber());
        for (TrainingUnitRes trainingUnitDTO : dayUnitDTO.getTrainingUnits()) {
            TrainingUnit trainingUnit;
            if(trainingUnitDTO.getId() == null){
                TrainingUnitDTO dto = modelMapper.map(trainingUnitDTO, TrainingUnitDTO.class);
                trainingUnit = createTrainingUnit(dto);
            }else {
                trainingUnit = updateTrainingUnit(trainingUnitDTO.getId(), trainingUnitDTO);
            }
            dayUnit.addTrainingUnit(trainingUnit);
        }
        return daysUnitRepository.save(dayUnit);
    }

    private TrainingUnit updateTrainingUnit(Long id, TrainingUnitRes trainingUnitDTO) {
        TrainingUnit trainingUnit = trainingUnitRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training Unit with id: " + id + " not found to update!!"));
        // Set properties for trainingUnit from trainingUnitDTO
        trainingUnit.setUnitNumber(trainingUnitDTO.getUnitNumber());
        trainingUnit.setUnitName(trainingUnitDTO.getUnitName());
        trainingUnit = trainingUnitRepository.save(trainingUnit);
        float totalDuration = 0.0F;
        for (TrainingContentRes trainingContentDTO : trainingUnitDTO.getTrainingContents()) {
            TrainingContent trainingContent;
            if(trainingContentDTO.getId() == null){
                TrainingContentDTO dto = modelMapper.map(trainingContentDTO, TrainingContentDTO.class);
                trainingContent = createTrainingContent(dto);
            }else {
                trainingContent = updateTrainingContent(trainingContentDTO.getId(), trainingContentDTO);
            }
            trainingUnit.addTrainingContent(trainingContent);
            totalDuration += trainingContent.getDuration();
        }
        trainingUnit.setTrainingTime(totalDuration / 60);
        return trainingUnitRepository.save(trainingUnit);
    }

    private TrainingContent updateTrainingContent(Long id, TrainingContentRes trainingContentDTO) {
        TrainingContent trainingContent = trainingContentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training Content with id: " + id + " not found to update!!!"));
        trainingContent.setOrderNumber(trainingContentDTO.getOrderNumber());
        trainingContent.setName(trainingContentDTO.getContentName());
        trainingContent.setDuration(trainingContentDTO.getDuration());
        trainingContent.setDeliveryType(trainingContentDTO.getDeliveryType());
        trainingContent.setMethod(trainingContentDTO.getMethod());
        Set<LearningObjective> objectiveSet = new HashSet<>();

        for (String objCode : trainingContentDTO.getOutputStandards()) {
            LearningObjective objective = learningObjectiveRepository.findByCode(objCode)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Output standard with code: " + objCode + " not found to update"));
            // Add the LearningObjective to the set
            objectiveSet.add(objective);
        }
        trainingContent.setObjectiveCodes(objectiveSet);
        return trainingContentRepository.save(trainingContent);
    }

    @Override
    public SyllabusDetailsRes deactiveSyllabus(String code) {
        Syllabus originalSyllabus = syllabusRepository.findByCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Syllabus not found to de-activated!!"));
        if(isSyllabusInUsed(originalSyllabus)) throw new ApiException(HttpStatus.CONFLICT,
                "You cannot change this syllabus status because this syllabus is in another active Training Program");
        originalSyllabus.setStatus(0);
        Syllabus deletedSyllabus = syllabusRepository.save(originalSyllabus);
        return syllabusConverter.convertToDetailsRes(deletedSyllabus);
    }

    private boolean isSyllabusInUsed(Syllabus syllabus){
        return !syllabus.getTrainingProgramSyllabusSet().isEmpty();
    }

    @Override
    public Page<SyllabusPageRes> searchSyllabus(String searchKey, List<String> createdBy, LocalDate startDate, LocalDate endDate,
                                                Integer duration, List<String> outputStandards, List<Integer> statuses, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Syllabus> syllabusPage;
        if(startDate != null) {
            if(endDate != null) syllabusPage = syllabusRepository.searchByDateNotNull(searchKey, createdBy, startDate, endDate, duration,
                                                                                        outputStandards, statuses, pageable);
            else throw new ApiException(HttpStatus.BAD_REQUEST, "End Date must not be null or empty");
        }else{
            if(endDate != null)  throw new ApiException(HttpStatus.BAD_REQUEST, "Start Date must not be null or empty");
            else syllabusPage = syllabusRepository.searchByDateNull(searchKey, createdBy, duration, outputStandards, statuses, pageable);
        }
        return syllabusPage.map(syllabusConverter::convertToPageRes);
    }

    @Override
    public SyllabusDetailsRes duplicateSyllabus(String code) {
        Syllabus originalSyllabus = syllabusRepository.findByCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Chosen syllabus not found!!"));
        Syllabus duplicateSyllabus = new Syllabus();
            int duplicateCounter = 0;
            String orginalCode = originalSyllabus.getCode();
            String newCode;
            do {
                duplicateCounter++;
                newCode = orginalCode + "_" + duplicateCounter;
//                duplicateSyllabus.setCode(orginalCode + "_" + duplicateCounter);
            } while (syllabusRepository.existsByCode(newCode));
            duplicateSyllabus.setCode(newCode);
            duplicateSyllabus.updateVersionDraft();
            duplicateSyllabus.setVersion(0);
            duplicateSyllabus.setName(originalSyllabus.getName());
            duplicateSyllabus.setStatus(2); // draft mode
            duplicateSyllabus.setModifiedBy(null);
            duplicateSyllabus.setModifiedDate(null);
            duplicateSyllabus.setCreatedDate();
            duplicateSyllabus.setCreatedBy(authenticationService.getName());
            duplicateSyllabus.setLevel(originalSyllabus.getLevel());
            duplicateSyllabus.setCourseObjectives(originalSyllabus.getCourseObjectives());
            duplicateSyllabus.setCourseObjectives(originalSyllabus.getCourseObjectives());
            duplicateSyllabus.setAttendeeNumber(originalSyllabus.getAttendeeNumber());
            duplicateSyllabus.setTechnicalRequirements(originalSyllabus.getTechnicalRequirements());
            duplicateSyllabus.setQuizAssessment(originalSyllabus.getQuizAssessment());
            duplicateSyllabus.setFinalAssessment(originalSyllabus.getFinalAssessment());
            duplicateSyllabus.setGpaCriteria(originalSyllabus.getGpaCriteria());
            duplicateSyllabus.setAssignmentAssessment(originalSyllabus.getAssignmentAssessment());
            duplicateSyllabus.setFinalTheoryAssessment(originalSyllabus.getFinalTheoryAssessment());
            duplicateSyllabus.setFinalPracticeAssessment(originalSyllabus.getFinalPracticeAssessment());
            duplicateSyllabus.setTrainingPrinciple(originalSyllabus.getTrainingPrinciple());
            duplicateSyllabus.setReTestPrinciple(originalSyllabus.getReTestPrinciple());
            duplicateSyllabus.setOthersPrinciple(originalSyllabus.getOthersPrinciple());
            duplicateSyllabus.setMarkingPrinciple(originalSyllabus.getMarkingPrinciple());
            duplicateSyllabus.setWaiverCriteriaPrinciple(originalSyllabus.getWaiverCriteriaPrinciple());

        for (DaysUnit daysUnit : originalSyllabus.getDaysUnits()) {
            DaysUnit duplicatedDaysUnit = duplicateDayUnit(daysUnit);
            duplicateSyllabus.addDayUnit(duplicatedDaysUnit);
        }
        Syllabus duplicatedSyllabus = syllabusRepository.save(duplicateSyllabus);
        return syllabusConverter.convertToDetailsRes(duplicatedSyllabus);
    }

    public DaysUnit duplicateDayUnit(DaysUnit dayUnit) {
        DaysUnit duplicatedDaysUnit = new DaysUnit();
        duplicatedDaysUnit.setDayNumber(dayUnit.getDayNumber());

        for (TrainingUnit trainingUnit : dayUnit.getTrainingUnits()) {
            TrainingUnit duplicatedTrainingUnit = duplicateTrainingUnit(trainingUnit);
            duplicatedDaysUnit.addTrainingUnit(duplicatedTrainingUnit);
        }

        return daysUnitRepository.save(duplicatedDaysUnit);
    }

    private TrainingUnit duplicateTrainingUnit(TrainingUnit trainingUnit) {
        TrainingUnit duplicatedTrainingUnit = new TrainingUnit();
        duplicatedTrainingUnit.setUnitNumber(trainingUnit.getUnitNumber());
        duplicatedTrainingUnit.setUnitName(trainingUnit.getUnitName());
        duplicatedTrainingUnit.setTrainingTime(trainingUnit.getTrainingTime());
        for (TrainingContent trainingContent : trainingUnit.getTrainingContents()) {
            TrainingContent duplicatedTrainingContent = duplicateTrainingContent(trainingContent);
            duplicatedTrainingUnit.addTrainingContent(duplicatedTrainingContent);
        }
        return trainingUnitRepository.save(duplicatedTrainingUnit);
    }

    public TrainingContent duplicateTrainingContent(TrainingContent trainingContent) {
        TrainingContent duplicatedTrainingContent = new TrainingContent();
        duplicatedTrainingContent.setOrderNumber(trainingContent.getOrderNumber());
        duplicatedTrainingContent.setName(trainingContent.getName());
        duplicatedTrainingContent.setDuration(trainingContent.getDuration());
        duplicatedTrainingContent.setDeliveryType(trainingContent.getDeliveryType());
        duplicatedTrainingContent.setMethod(trainingContent.getMethod());

        Set<LearningObjective> objectiveSet = new HashSet<>(trainingContent.getObjectiveCodes());
        duplicatedTrainingContent.setObjectiveCodes(objectiveSet);
        duplicatedTrainingContent.setLearningMaterials(new HashSet<>());
        return trainingContentRepository.save(duplicatedTrainingContent);
    }

    public List<ActiveSyllabus> getActiveSyllabusList(String name){
        List<Syllabus> activeList;
        if(name == null) {
            activeList = syllabusRepository.findAllByStatus(1);
        }else {
            activeList = syllabusRepository.findAllByNameContainingIgnoreCaseAndStatus(name, 1);
        }
        return activeList.stream().map(syllabusConverter::convertToActiveList).toList();
    }

}
