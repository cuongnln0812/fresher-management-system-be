package com.example.phase1_fams.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.*;

import com.example.phase1_fams.converter.ClassConverter;
import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.model.Class;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.TrainingProgramConverter;
import com.example.phase1_fams.dto.TrainingProgramDTO;
import com.example.phase1_fams.dto.TrainingProgramSyllabusDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.TrainingProgramReq;
import com.example.phase1_fams.dto.request.TrainingProgramReqUpdate;
import com.example.phase1_fams.dto.response.TrainingProgramRes;
import com.example.phase1_fams.repository.SyllabusRepository;
import com.example.phase1_fams.repository.TrainingProgramRepository;
import com.example.phase1_fams.repository.TrainingProgramSyllabusRepository;
import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.service.TrainingProgramService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TrainingProgramServiceImpl implements TrainingProgramService {
    private final TrainingProgramRepository trainingProgramRepository;
    private final AuthenticationService authenticationService;
    private final SyllabusRepository syllabusRepository;
    private final UsersRepository usersRepository;
    private final TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;
    private final TrainingProgramConverter trainingProgramConverter;
    private final ClassConverter classConverter;

    public TrainingProgramServiceImpl(
            AuthenticationService authenticationService,
            TrainingProgramRepository trainingProgramRepository,
            SyllabusRepository syllabusRepository,
            UsersRepository usersRepository,
            TrainingProgramSyllabusRepository trainingProgramSyllabusRepository,
            TrainingProgramConverter trainingProgramConverter, ClassConverter classConverter) {
        this.authenticationService = authenticationService;
        this.trainingProgramRepository = trainingProgramRepository;
        this.syllabusRepository = syllabusRepository;
        this.usersRepository = usersRepository;
        this.trainingProgramSyllabusRepository = trainingProgramSyllabusRepository;
        this.trainingProgramConverter = trainingProgramConverter;
        this.classConverter = classConverter;
    }

    @Override
    public TrainingProgramRes duplicateTrainingProgram(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not log in or not found!"));
        String name = user.getName();

        TrainingProgram trainingProgram = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this training program"));

        TrainingProgram duplicatedTrainingProgram = new TrainingProgram();
        BeanUtils.copyProperties(trainingProgram, duplicatedTrainingProgram, "id", "createdDate", "createdBy",
                "modifiedDate", "modifiedBy", "classes", "trainingProgramSyllabusSet");
        int duplicateCounter = 0;
        String orginalName = trainingProgram.getName();
        do {
            duplicateCounter++;
            duplicatedTrainingProgram.setName(orginalName + "_" + duplicateCounter);
        } while (!isNameUnique(duplicatedTrainingProgram.getName()));
        //tạo trc 1 duplicate training program chưa có syllabus
        duplicatedTrainingProgram.setStatus(2);
        duplicatedTrainingProgram.setModifiedBy(null);
        duplicatedTrainingProgram.setModifiedDate(null);
        duplicatedTrainingProgram.setCreatedDate();
        duplicatedTrainingProgram.setCreatedBy(name);
        TrainingProgram savedDup = trainingProgramRepository.save(duplicatedTrainingProgram); //-> có id của dupTrainingProgram
        Set<TrainingProgramSyllabus> duplicatedTrainingProgramSyllabusSet = new HashSet<>();
        for (TrainingProgramSyllabus trainingProgramSyllabus : trainingProgram.getTrainingProgramSyllabusSet()) { //lấy ra từng cái trainingProgramSyllabus của OG
            TrainingProgramSyllabus duplicateTrainingSyllabus = duplicatedTrainingProgramSyllabus(trainingProgramSyllabus, savedDup);
            duplicatedTrainingProgramSyllabusSet.add(duplicateTrainingSyllabus);
        }
        savedDup.setTrainingProgramSyllabusSet(duplicatedTrainingProgramSyllabusSet);
        TrainingProgram saved = trainingProgramRepository.save(savedDup);
        return trainingProgramConverter.convertToTrainingProgramRes(saved);

    }

    public TrainingProgramSyllabus duplicatedTrainingProgramSyllabus(TrainingProgramSyllabus trainingProgramSyllabus,
                                                                     TrainingProgram duplicatedTrainingProgram) {
        TrainingProgramSyllabus duplicatedTrainingProgramSyllabus = new TrainingProgramSyllabus();
        duplicatedTrainingProgramSyllabus.setId(new TrainingProgramSyllabusKey(
                trainingProgramSyllabus.getId().getTopicCode(), duplicatedTrainingProgram.getId()));
        duplicatedTrainingProgramSyllabus.setSyllabus(trainingProgramSyllabus.getSyllabus());
        duplicatedTrainingProgramSyllabus.setTrainingProgram(duplicatedTrainingProgram);
        duplicatedTrainingProgramSyllabus.setSequence(trainingProgramSyllabus.getSequence());
        return trainingProgramSyllabusRepository.save(duplicatedTrainingProgramSyllabus);
    }

    private boolean isNameUnique(String name) {
        Optional<TrainingProgram> existingCode = trainingProgramRepository.findByName(name);
        return existingCode.isEmpty();
    }

    @Override
    public TrainingProgramRes getTrainingProgramDetails(Long id) {

        TrainingProgram trainingProgram = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training Program not found!"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find who is logged in"));

        if ((users.getRole().getRoleId() == 3) && (trainingProgram.getStatus() != 1))
            throw new ApiException(HttpStatus.UNAUTHORIZED,
                    "You dont have the authority to access to this Training Program!");

        return trainingProgramConverter.convertToTrainingProgramRes(trainingProgram);
    }

    @Override
    public Page<ClassDTO> getClassListByTrainingProgram(Long id, int page, int size){
        TrainingProgram trainingProgram = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training Program not found!"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find who is logged in"));

        if ((users.getRole().getRoleId() == 3) && (trainingProgram.getStatus() != 1))
            throw new ApiException(HttpStatus.UNAUTHORIZED,
                    "You dont have the authority to access to this Training Program!");
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Class> classes = trainingProgramRepository.getAllClassByTrainingProgram(id, pageRequest);

        return classes.map(classConverter::convertToPageRes);
    }

    @Override
    public TrainingProgramRes switchStatus(Long trainingProgramId) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProgramId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training Program not found to be deleted!"));
        if(isTrainingProgramInUsed(trainingProgram)) throw new ApiException(HttpStatus.CONFLICT,
                "You cannot change this Training Program status because it is in used in one or many Classes");
            //De-activate
        if(trainingProgram.getStatus() == 1 ) trainingProgram.setStatus(0);
        else if(trainingProgram.getStatus() == 0) trainingProgram.setStatus(1);
        else throw new ApiException(HttpStatus.BAD_REQUEST, "Error set de-activate/activate for training program with DRAFTING status");
        trainingProgram = trainingProgramRepository.save(trainingProgram);
        return trainingProgramConverter.convertToTrainingProgramRes(trainingProgram);
    }

    private boolean isTrainingProgramInUsed(TrainingProgram trainingProgram){
        return (!trainingProgram.getClasses().isEmpty());
    }

    @Override
    public InputStreamResource downloadFileFromGoogleDrive(String googleDriveLink) {
        try {
            URL url = new URL(googleDriveLink);
            URLConnection connection = url.openConnection();
            return new InputStreamResource(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void importTrainingProgram(MultipartFile file, Integer duplicateOption) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(formatter.formatCellValue(cell));
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                TrainingProgram trainingProgram = new TrainingProgram();
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = formatter.formatCellValue(cell);
                    if (j < headers.size()) {
                        String header = headers.get(j);
                        if (!header.isEmpty()) {
                            switch (header) {
                                case "Name":
                                    if (!cellValue.isEmpty()) {
                                        // Create a new TrainingProgram instance
                                        trainingProgram.setName(cellValue);
                                    }
                                    break;
                                case "Information":
                                    if (!cellValue.isEmpty()) {
                                        trainingProgram.setDescription(cellValue);
                                    }
                                    break;
                                case "List Syllabus":
                                    int duration = 0;
                                    int sequence = 1;
                                    Set<TrainingProgramSyllabus> syllabusCodes = new HashSet<>();
                                    for (String syllabusCode : cellValue.split(",")) {
                                        Syllabus syllabus = syllabusRepository.findByCode(syllabusCode)
                                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                                        "Syllabus code not found for code: " + syllabusCode));

                                        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus();
                                        TrainingProgramSyllabusKey trainingProgramSyllabusKey = new TrainingProgramSyllabusKey();
                                        trainingProgramSyllabusKey.setTopicCode(syllabusCode);
                                        trainingProgramSyllabusKey.setTrainingProgramId(trainingProgram.getId());
                                        duration = duration + syllabus.getDaysUnits().size();
                                        trainingProgramSyllabus.setSequence(sequence++);
                                        trainingProgramSyllabus.setId(trainingProgramSyllabusKey);
                                        trainingProgramSyllabus.setSyllabus(syllabus);
                                        trainingProgramSyllabus.setTrainingProgram(trainingProgram);
                                        syllabusCodes.add(trainingProgramSyllabus);
                                    }
                                    trainingProgram.setStatus(2);
                                    trainingProgram.setDuration(duration);
                                    trainingProgram.setTrainingProgramSyllabusSet(syllabusCodes);
                                    break;
                            }
                        }
                    }

                }

                trainingProgram.setCreatedBy(authenticationService.getName());
                trainingProgram.setCreatedDate();
                // Check if a training program with the same name already exists

                if (!isNameUnique(trainingProgram.getName())) {

                    switch (duplicateOption) {
                        case 1:
                            int duplicateCounter = 0;
                            String originalName = trainingProgram.getName();

                            while (!isNameUnique(trainingProgram.getName())) {
                                duplicateCounter++;
                                trainingProgram.setName(originalName + "_" + duplicateCounter);
                            }
                            trainingProgramRepository.save(trainingProgram);
                            break;
                        case 2:
                            trainingProgramRepository.findByName(trainingProgram.getName())
                                    .ifPresent(existingProgram -> trainingProgramRepository.delete(existingProgram));
                            trainingProgramRepository.save(trainingProgram);
                            break;
                        case 0:
                            break;
                        default:
                            throw new ApiException(HttpStatus.BAD_REQUEST,
                                    "Duplicate option must be 1 or 2 or 0 (allow, replace, skip)");
                    }
                } else {
                    trainingProgramRepository.save(trainingProgram);
                }
            }

        } catch (
        IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importTrainingProgramCSV(MultipartFile file, Integer duplicateOption) {
        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            DataFormatter formatter = new DataFormatter();
            List<String> headers = new ArrayList<>();

            if ((line = reader.readLine()) != null) {
                // Process headers
                String[] headerArray = line.split(",");

                // Remove non-alphanumeric characters from each header
                for (String header : headerArray) {
                    headers.add(header.replaceAll("[^a-zA-Z0-9]", ""));
                }

                // Perform additional checks if needed
                if (headers.contains("")) {
                    // Handle case where there is an empty header
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Header cannot be empty");
                }
            }

            while ((line = reader.readLine()) != null) {
                TrainingProgram trainingProgram = null;
                String[] values = line.split(",");
                for (int j = 0; j < values.length; j++) {
                    String cellValue = values[j];
                    if (j < headers.size()) {
                        String header = headers.get(j);
                        if (!header.isEmpty()) {
                            switch (header) {
                                case "Name":
                                    if (!cellValue.isEmpty()) {
                                        trainingProgram = new TrainingProgram();
                                        trainingProgram.setName(cellValue);
                                    }
                                    break;
                                case "Information":
                                    if (!cellValue.isEmpty()) {
                                        if (trainingProgram == null) {
                                            throw new ApiException(HttpStatus.BAD_REQUEST,
                                                    "Name must be provided before Information");
                                        }
                                        trainingProgram.setDescription(cellValue);
                                    }
                                    break;
                                case "ListSyllabus":
                                    if (trainingProgram == null) {
                                        throw new ApiException(HttpStatus.BAD_REQUEST,
                                                "Name must be provided before List Syllabus");
                                    }
                                    Set<TrainingProgramSyllabus> syllabusCodes = new HashSet<>();
                                    int duration = 0;
                                    for (String syllabusCode : cellValue.split(",")) {
                                        Syllabus syllabus = syllabusRepository.findByCode(syllabusCode)
                                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                                        "Syllabus code not found for code: " + syllabusCode));
                                        duration = duration + syllabus.getDaysUnits().size();
                                        TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus();
                                        TrainingProgramSyllabusKey trainingProgramSyllabuskey = new TrainingProgramSyllabusKey();
                                        trainingProgramSyllabuskey.setTopicCode(syllabusCode);
                                        trainingProgramSyllabuskey.setTrainingProgramId(trainingProgram.getId());
                                        trainingProgramSyllabus.setId(trainingProgramSyllabuskey);
                                        trainingProgramSyllabus.setSyllabus(syllabus);
                                        trainingProgramSyllabus.setTrainingProgram(trainingProgram);
                                        syllabusCodes.add(trainingProgramSyllabus);
                                    }
                                    trainingProgram.setStatus(2);
                                    trainingProgram.setDuration(duration);
                                    trainingProgram.setTrainingProgramSyllabusSet(syllabusCodes);
                                    break;
                            }
                        }
                    }
                }
                if (trainingProgram != null) {
                    trainingProgram.setCreatedBy(authenticationService.getName());
                    trainingProgram.setCreatedDate();
                    if (!isNameUnique(trainingProgram.getName())) {
                        switch (duplicateOption) {
                            case 1:
                                int duplicateCounter = 0;
                                String originalName = trainingProgram.getName();

                                while (!isNameUnique(trainingProgram.getName())) {
                                    duplicateCounter++;
                                    trainingProgram.setName(originalName + "_" + duplicateCounter);
                                }
                                trainingProgramRepository.save(trainingProgram);
                                break;
                            case 2:
                                trainingProgramRepository.findByName(trainingProgram.getName())
                                        .ifPresent(
                                                existingProgram -> trainingProgramRepository.delete(existingProgram));
                                trainingProgramRepository.save(trainingProgram);
                                break;
                            case 0:
                                // Handle skipping as needed
                                break;
                            default:
                                throw new ApiException(HttpStatus.BAD_REQUEST,
                                        "Duplicate option must be 1 or 2 or 0 (allow, replace, skip)");
                        }
                    } else {
                        trainingProgramRepository.save(trainingProgram);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TrainingProgramRes createTrainingProgramAsActive(TrainingProgramReq trainingProgramReq) {
        if (trainingProgramReq.getTrainingProgramDTOSet().isEmpty())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Syllabus list is empty!!!");
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setName(trainingProgramReq.getName());
        trainingProgram.setDuration(trainingProgramReq.getDuration());
        trainingProgram.setStatus(1);
        trainingProgram.setCreatedBy(authenticationService.getName());
        trainingProgram.setCreatedDate();
        trainingProgram.setDescription(trainingProgramReq.getDescription());
        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        TrainingProgram newTrainingProgram = trainingProgramRepository.save(trainingProgram);
        for (TrainingProgramSyllabusDTO trainingProgramSyllabusDTO : trainingProgramReq.getTrainingProgramDTOSet()) {
            Syllabus syllabus = syllabusRepository.findByCode(trainingProgramSyllabusDTO.getSyllabusCode())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Syllabus not found to create"));
            if (syllabus.getStatus() != 1)
                throw new ApiException(HttpStatus.BAD_REQUEST, "Only syllabuses with Active status can used to create");
            TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(syllabus.getCode(),
                    newTrainingProgram.getId());
            TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus();
            trainingProgramSyllabus.setId(key);
            trainingProgramSyllabus.setSequence(trainingProgramSyllabusDTO.getSequence());
            trainingProgramSyllabus.setTrainingProgram(newTrainingProgram);
            trainingProgramSyllabus.setSyllabus(syllabus);
            TrainingProgramSyllabus newTrainingProgramSyllabus = trainingProgramSyllabusRepository
                    .save(trainingProgramSyllabus);
            trainingProgramSyllabusSet.add(newTrainingProgramSyllabus);
        }
        newTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        return trainingProgramConverter.convertToTrainingProgramRes(newTrainingProgram);

    }

    @Override
    public TrainingProgramRes createTrainingProgramAsDraft(TrainingProgramReq trainingProgramReq) {
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setName(trainingProgramReq.getName());
        trainingProgram.setDuration(trainingProgramReq.getDuration());
        trainingProgram.setStatus(2);
        trainingProgram.setCreatedBy(authenticationService.getName());
        trainingProgram.setCreatedDate();
        trainingProgram.setDescription(trainingProgramReq.getDescription());
        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        TrainingProgram newTrainingProgram = trainingProgramRepository.save(trainingProgram);
        for (TrainingProgramSyllabusDTO trainingProgramSyllabusDTO : trainingProgramReq.getTrainingProgramDTOSet()) {
            Syllabus syllabus = syllabusRepository.findByCode(trainingProgramSyllabusDTO.getSyllabusCode())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Syllabus not found to create"));
            if (syllabus.getStatus() != 1)
                throw new ApiException(HttpStatus.BAD_REQUEST, "Only syllabuses with Active status can used to create");
            TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(syllabus.getCode(),
                    newTrainingProgram.getId());
            TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus();
            trainingProgramSyllabus.setId(key);
            trainingProgramSyllabus.setSequence(trainingProgramSyllabusDTO.getSequence());
            trainingProgramSyllabus.setTrainingProgram(newTrainingProgram);
            trainingProgramSyllabus.setSyllabus(syllabus);
            TrainingProgramSyllabus newTrainingProgramSyllabus = trainingProgramSyllabusRepository
                    .save(trainingProgramSyllabus);
            trainingProgramSyllabusSet.add(newTrainingProgramSyllabus);
        }
        newTrainingProgram.setTrainingProgramSyllabusSet(trainingProgramSyllabusSet);
        return trainingProgramConverter.convertToTrainingProgramRes(newTrainingProgram);
    }

    @Override
    public TrainingProgramRes updateTrainingProgramAsActive(TrainingProgramReqUpdate trainingProgramReqUpdate) {
        TrainingProgram updatedTrainingProgram = trainingProgramRepository.findById(trainingProgramReqUpdate.getId())
                .orElseThrow(
                        () -> new ApiException(HttpStatus.NOT_FOUND, "This training program is not found to update"));
        updatedTrainingProgram.setName(trainingProgramReqUpdate.getName());
        updatedTrainingProgram.setDuration(trainingProgramReqUpdate.getDuration());
        updatedTrainingProgram.setStatus(1);
        updatedTrainingProgram.setModifiedBy(authenticationService.getName());
        updatedTrainingProgram.setModifiedDate();
        updatedTrainingProgram.setDescription(trainingProgramReqUpdate.getDescription());
        TrainingProgram newTrainingProgram = trainingProgramRepository.save(updatedTrainingProgram);
        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = trainingProgramSyllabusRepository
                .findByTrainingProgramId(trainingProgramReqUpdate.getId());
        trainingProgramSyllabusRepository.deleteAll(trainingProgramSyllabusSet);

        Set<TrainingProgramSyllabus> newSet = new HashSet<>();
        for (TrainingProgramSyllabusDTO trainingProgramSyllabusDTO : trainingProgramReqUpdate
                .getTrainingProgramDTOSet()) {
            Syllabus syllabus = syllabusRepository.findByCode(trainingProgramSyllabusDTO.getSyllabusCode())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Syllabus not found to create"));
            TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(syllabus.getCode(),
                    newTrainingProgram.getId());
            TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus();
            trainingProgramSyllabus.setId(key);
            trainingProgramSyllabus.setSequence(trainingProgramSyllabusDTO.getSequence());
            trainingProgramSyllabus.setTrainingProgram(newTrainingProgram);
            trainingProgramSyllabus.setSyllabus(syllabus);
            TrainingProgramSyllabus newTrainingProgramSyllabus = trainingProgramSyllabusRepository
                    .save(trainingProgramSyllabus);
            newSet.add(newTrainingProgramSyllabus);
        }
        newTrainingProgram.setTrainingProgramSyllabusSet(newSet);
        if(newTrainingProgram.getTrainingProgramSyllabusSet().isEmpty())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Syllabus is required to update as active");

        return trainingProgramConverter.convertToTrainingProgramRes(newTrainingProgram);

    }

    @Override
    public TrainingProgramRes updateTrainingProgramAsDraft(TrainingProgramReqUpdate trainingProgramReqUpdate) {
        TrainingProgram updatedTrainingProgram = trainingProgramRepository.findById(trainingProgramReqUpdate.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "This training program is not found to update"));
        if(isTrainingProgramInUsed(updatedTrainingProgram)) throw new ApiException(HttpStatus.CONFLICT,
                "You cannot update as draft for this Training Program status because it is in used in one or many Classes");
        updatedTrainingProgram.setName(trainingProgramReqUpdate.getName());
        updatedTrainingProgram.setDuration(trainingProgramReqUpdate.getDuration());
        updatedTrainingProgram.setStatus(2);
        updatedTrainingProgram.setModifiedBy(authenticationService.getName());
        updatedTrainingProgram.setModifiedDate();
        updatedTrainingProgram.setDescription(trainingProgramReqUpdate.getDescription());

        TrainingProgram newTrainingProgram = trainingProgramRepository.save(updatedTrainingProgram);

        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = trainingProgramSyllabusRepository
                .findByTrainingProgramId(trainingProgramReqUpdate.getId());
        trainingProgramSyllabusRepository.deleteAll(trainingProgramSyllabusSet);
        Set<TrainingProgramSyllabus> newSet = new HashSet<>();

        for (TrainingProgramSyllabusDTO trainingProgramSyllabusDTO : trainingProgramReqUpdate
                .getTrainingProgramDTOSet()) {
            Syllabus syllabus = syllabusRepository.findByCode(trainingProgramSyllabusDTO.getSyllabusCode())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Syllabus not found to create"));
            TrainingProgramSyllabusKey key = new TrainingProgramSyllabusKey(syllabus.getCode(),
                    newTrainingProgram.getId());
            TrainingProgramSyllabus trainingProgramSyllabus = new TrainingProgramSyllabus();
            trainingProgramSyllabus.setId(key);
            trainingProgramSyllabus.setSequence(trainingProgramSyllabusDTO.getSequence());
            trainingProgramSyllabus.setTrainingProgram(newTrainingProgram);
            trainingProgramSyllabus.setSyllabus(syllabus);
            TrainingProgramSyllabus newTrainingProgramSyllabus = trainingProgramSyllabusRepository
                    .save(trainingProgramSyllabus);
            newSet.add(newTrainingProgramSyllabus);
        }
        newTrainingProgram.setTrainingProgramSyllabusSet(newSet);

        return trainingProgramConverter.convertToTrainingProgramRes(newTrainingProgram);
    }
    @Override
    public Page<TrainingProgramDTO> searchTrainingPrograms(String keyword, List<String> createdBy, LocalDate startDate, LocalDate endDate,
                                                           Integer duration, List<Integer> statuses, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not log in or not found!"));
        Role role = user.getRole();
        Page<TrainingProgram> trainingPrograms;
        if (startDate != null) {
            if(endDate != null) {
                if(role.getRoleId() != 3) trainingPrograms = trainingProgramRepository.searchWithDateNotNull(keyword, createdBy, startDate, endDate,
                        duration, statuses, pageable);
                else trainingPrograms = trainingProgramRepository.searchWithDateNotNullWithStatusActive(keyword, createdBy, startDate, endDate,
                        duration, statuses, pageable);
            }else throw new ApiException(HttpStatus.BAD_REQUEST, "End Date must not be null or empty");
        }else {
            if (endDate != null) throw new ApiException(HttpStatus.BAD_REQUEST, "Start Date must not be null or empty");
            else {
                if(role.getRoleId() != 3) trainingPrograms = trainingProgramRepository.searchWithDateNull(keyword, createdBy, duration, statuses, pageable);
                else trainingPrograms = trainingProgramRepository.searchWithDateNullWithStatusActive(keyword, createdBy, duration, statuses, pageable);
            }
        }
        return trainingPrograms.map(trainingProgramConverter::convertToPageRes);
    }

    @Override
    public List<TrainingProgramDTO> getActiveTrainingProgramList(String name) {
        List<TrainingProgram> activeList;
        if(name == null){
            activeList = trainingProgramRepository.findAllByStatus(1);
        }else {
            activeList = trainingProgramRepository.findAllByNameContainingIgnoreCaseAndStatus(name, 1);
        }
        return activeList.stream().map(trainingProgramConverter::convertToPageRes).toList();
    }
}
