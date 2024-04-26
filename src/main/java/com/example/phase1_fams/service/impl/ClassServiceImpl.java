package com.example.phase1_fams.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.example.phase1_fams.dto.request.ClassReqUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.ClassConverter;
import com.example.phase1_fams.converter.TrainingProgramConverter;
import com.example.phase1_fams.dto.AttendeeDTO;
import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.ClassReq;
import com.example.phase1_fams.dto.request.ClassUserReq;
import com.example.phase1_fams.dto.request.SessionReq;
import com.example.phase1_fams.dto.response.ClassDetailsRes;
import com.example.phase1_fams.dto.response.ClassRes;
import com.example.phase1_fams.dto.response.ClassUserRes;
import com.example.phase1_fams.dto.response.SessionRes;
import com.example.phase1_fams.model.Class;
import com.example.phase1_fams.model.ClassUser;
import com.example.phase1_fams.model.ClassUserKey;
import com.example.phase1_fams.model.Session;
import com.example.phase1_fams.model.TrainingProgram;
import com.example.phase1_fams.model.Users;
import com.example.phase1_fams.repository.ClassRepository;
import com.example.phase1_fams.repository.ClassUserRepository;
import com.example.phase1_fams.repository.SessionRepository;
import com.example.phase1_fams.repository.TrainingProgramRepository;
import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.service.ClassService;


@Service
@Transactional
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;

    private final TrainingProgramRepository trainingProgramRepository;

    private final SessionRepository sessionRepository;

    private final UsersRepository usersRepository;

    private final ClassUserRepository classUserRepository;

    private final TrainingProgramConverter trainingProgramConverter;

    private final ClassConverter classConverter;

    private final AuthenticationService authenticationService;

    @Autowired
    public ClassServiceImpl(ClassRepository classRepository, TrainingProgramRepository trainingProgramRepository,
                            SessionRepository sessionRepository, UsersRepository usersRepository,
                            ClassUserRepository classUserRepository, TrainingProgramConverter trainingProgramConverter,
                            ClassConverter classConverter, AuthenticationService authenticationService) {
        this.classRepository = classRepository;
        this.trainingProgramRepository = trainingProgramRepository;
        this.sessionRepository = sessionRepository;
        this.usersRepository = usersRepository;
        this.classUserRepository = classUserRepository;
        this.trainingProgramConverter = trainingProgramConverter;
        this.classConverter = classConverter;
        this.authenticationService = authenticationService;
    }

    int incrementalNumber = 1;

    @Override
    public Page<ClassDTO> findFilteredClasses(
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
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Class> classes;
        if(fromDates != null) {
            if(toDates != null) classes = classRepository.findFilteredClassesWhenFromDateAndToDateNotNull(keywords, locations, attendeeTypes, fromDates,
                                                                                                            toDates, times, statuses, fsus, pageRequest);
            else throw new ApiException(HttpStatus.BAD_REQUEST, "End Date must not be null or empty");
        }else{
            if(toDates != null)  throw new ApiException(HttpStatus.BAD_REQUEST, "Start Date must not be null or empty");
            else classes = classRepository.findFilteredClassesWhenFromDateAndToDateNull(keywords, locations, attendeeTypes, times, statuses, fsus, pageRequest);
        }
        return classes.map(classConverter::convertToPageRes);
    }

    @Override
    public void deactivateClass(Long classId) {
        Class aClass = classRepository.findById(classId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Class not found"));
        if ("Planning".equals(aClass.getStatus()) || "Scheduled".equals(aClass.getStatus())) {
            aClass.setStatus("Inactive");
            classRepository.save(aClass);
        }else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Class status is not eligible for deactivation");
        }
    }

    @Override
    public ClassDetailsRes getClassDetails(Long classId) {
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Class not found!"));
        return classConverter.convertToDetailsRes(aClass);
    }

    @Override
    public List<SessionRes> updateAllSessionsInClass(Long classId, SessionReq sessionReq) {
        // Retrieve the class entity from the repository
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Class not found with id: " + classId));
        if(existingClass.getStatus().equals("Inactive") || existingClass.getStatus().equals("Closed"))
            throw new ApiException(HttpStatus.BAD_REQUEST, "You can not update this class sessions because the class is Inactive or Closed");
        existingClass.setFsu(sessionReq.getFsu());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(sessionReq.getStart(), formatter);
        LocalTime endTime = LocalTime.parse(sessionReq.getEnd(), formatter);
        isStartTimeAndEndTimeValid(startTime, endTime);
        String classTime = setClassTimeBaseOnStartTime(startTime);
        existingClass.setStartTime(startTime);
        existingClass.setEndTime(endTime);
        existingClass.setClassTime(classTime);
        Set<ClassUser> updatedClassUserSet = new HashSet<>();
        classUserRepository.deleteAllByaClass(existingClass);
        //Set admin
        Users admin = usersRepository.findById(sessionReq.getAdminId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (admin.getRole().getRoleId() == 2){
            ClassUserKey key = new ClassUserKey(admin.getId(), existingClass.getId());
            ClassUser adminUser = new ClassUser();
            adminUser.setId(key);
            adminUser.setUserType(admin.getRole().getRoleName());
            adminUser.setAClass(existingClass);
            adminUser.setUsers(admin);
            ClassUser newAdmin = classUserRepository.save(adminUser);
            updatedClassUserSet.add(newAdmin);
        }
        //Set trainer
        Users trainer = usersRepository.findById(sessionReq.getTrainerId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (trainer.getRole().getRoleId() == 3){
            ClassUserKey key = new ClassUserKey(trainer.getId(), existingClass.getId());
            ClassUser trainerUser = new ClassUser();
            trainerUser.setId(key);
            trainerUser.setUserType(trainer.getRole().getRoleName());
            trainerUser.setAClass(existingClass);
            trainerUser.setUsers(trainer);
            ClassUser newTrainer = classUserRepository.save(trainerUser);
            updatedClassUserSet.add(newTrainer);
        }
        existingClass.setClassUserType(updatedClassUserSet);
        for (Session x : existingClass.getSessions()) {
            if (admin.getRole().getRoleId() == 2) x.setAdminName(admin.getName());
            if (trainer.getRole().getRoleId() == 3) x.setTrainerName(trainer.getName());
            x.setClassTime(classTime);
            x.setStartTime(startTime);
            x.setEndTime(endTime);
            x.setAClass(existingClass);
            sessionRepository.save(x);
        }
        Class updated = classRepository.save(existingClass);
        return updated.getSessions().stream().map(this::convertToSessionRes).toList();
    }




    @Override
    public ClassRes createClassAsScheduled(ClassReq classReq) {
        Class aClass = new Class();
        if(classReq.getTrainingProgramId() == null) throw new ApiException(HttpStatus.BAD_REQUEST, "Training Program must not be blank");
        if(classReq.getListOfSessionDate().isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "Session date list must not be empty");
        if(classReq.getClassUserDTOSet().isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "Trainer and Admin must not be null");
        String code = String.format("%s_%d_%02d", classReq.getLocationCode().toUpperCase(), Year.now().getValue() % 100, incrementalNumber++);
        List<Class> classList = classRepository.findAll();
        for (Class x : classList) {
            if (x.getCode() != null){
                if (x.getCode().equalsIgnoreCase(code)) {
                    code = String.format("%s_%d_%02d", classReq.getLocationCode().toUpperCase(), Year.now().getValue() % 100, incrementalNumber++);
                }
            }
        }
        aClass.setCode(code);
        aClass.setName(classReq.getName());
        aClass.setLocation(classReq.getLocation());
        aClass.setFsu(classReq.getFsu());
        aClass.setStatus("Scheduled");
        aClass.setAttendeeType(classReq.getAttendeeDTO().getType());
        aClass.setPlannedAttendee(classReq.getAttendeeDTO().getPlanned());
        aClass.setAcceptedAttendee(classReq.getAttendeeDTO().getAccepted());
        aClass.setActualAttendee(classReq.getAttendeeDTO().getActual());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(classReq.getStartTime(), formatter);
        LocalTime endTime = LocalTime.parse(classReq.getEndTime(), formatter);
        isStartTimeAndEndTimeValid(startTime, endTime);
        String classTime = setClassTimeBaseOnStartTime(startTime);
        aClass.setClassTime(classTime);
        aClass.setStartTime(startTime);
        aClass.setEndTime(endTime);
        aClass.setCreatedBy(authenticationService.getName());
        aClass.setCreatedDate();
        aClass.setAttendeeType(classReq.getAttendeeDTO().getType());
        TrainingProgram trainingProgram = trainingProgramRepository.findByIdAndStatus(classReq.getTrainingProgramId(), 1)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this training program with status Active"));
        aClass.setDuration(trainingProgram.getDuration());
        aClass.setTrainingProgram(trainingProgram);
        trainingProgram.getClasses().add(aClass);
        Class newClass = classRepository.save(aClass);
        Set<ClassUser> classUserSet = new HashSet<>();
        for (ClassUserReq x : classReq.getClassUserDTOSet()) {
            Users users = usersRepository.findById(x.getUserId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this user"));
            ClassUserKey key = new ClassUserKey(users.getId(), newClass.getId());
            ClassUser classUser = new ClassUser();
            classUser.setId(key);
            classUser.setUserType(x.getUserType());
            classUser.setAClass(newClass);
            classUser.setUsers(users);
            ClassUser newClassUser = classUserRepository.save(classUser);
            classUserSet.add(newClassUser);
        }
        newClass.setClassUserType(classUserSet);
        Set<Session> sessionSet = new HashSet<>();
        List<LocalDate> sessionDateList = classReq.getListOfSessionDate();
        if(sessionDateList.size() != newClass.getDuration())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Calendar dates list is not equal to Class duration (Training Program duration)");
        Collections.sort(classReq.getListOfSessionDate());
        int dayProgress = 1;
        for (LocalDate x : classReq.getListOfSessionDate()) {
            Session session = new Session();
            for (ClassUserReq y : classReq.getClassUserDTOSet()) {
                if (y.getUserType().trim().equalsIgnoreCase("class admin")) {
                    Users users = usersRepository.findById(y.getUserId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this user"));
                    session.setAdminName(users.getName());
                }
                if (y.getUserType().trim().equalsIgnoreCase("trainer")) {
                    Users users = usersRepository.findById(y.getUserId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this user"));
                    session.setTrainerName(users.getName());
                }
            }
            session.setLocation(classReq.getLocation());
            session.setSessionDate(x);
            session.setDayProgress(dayProgress++);
            session.setTotalDays(classReq.getListOfSessionDate().size());
            session.setStartTime(startTime);
            session.setEndTime(endTime);
            session.setAClass(newClass);
            Session newSession = sessionRepository.save(session);
            sessionSet.add(newSession);
        }
        newClass.setSessions(sessionSet);
        newClass = classRepository.save(newClass);
        return convertToClassRes(newClass);
    }

    @Override
    public ClassRes createClassAsPlanning(ClassReq classReq) {
        Class aClass = new Class();
        if (classReq.getLocationCode() != null) {
            if (!classReq.getLocationCode().isEmpty()){
                String code = String.format("%s_%d_%02d", classReq.getLocationCode().toUpperCase(), Year.now().getValue() % 100, incrementalNumber++);
                List<Class> classList = classRepository.findAll();
                for (Class x : classList) {
                    if(x.getCode() != null) {
                        if (x.getCode().equalsIgnoreCase(code)) {
                            code = String.format("%s_%d_%02d", classReq.getLocationCode().toUpperCase(), Year.now().getValue() % 100, incrementalNumber++);
                        }
                    }
                }
                aClass.setCode(code);
            }
        }
        aClass.setName(classReq.getName());
        aClass.setLocation(classReq.getLocation());
        aClass.setFsu(classReq.getFsu());
        aClass.setStatus("Planning");
        aClass.setAttendeeType(classReq.getAttendeeDTO().getType());
        aClass.setPlannedAttendee(classReq.getAttendeeDTO().getPlanned());
        aClass.setAcceptedAttendee(classReq.getAttendeeDTO().getAccepted());
        aClass.setActualAttendee(classReq.getAttendeeDTO().getActual());
        LocalTime startTime = null;
        LocalTime endTime = null;
        String classTime = null;
        if (classReq.getEndTime() != null && classReq.getStartTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            startTime = LocalTime.parse(classReq.getStartTime(), formatter);
            endTime = LocalTime.parse(classReq.getEndTime(), formatter);
            isStartTimeAndEndTimeValid(startTime, endTime);
            classTime = setClassTimeBaseOnStartTime(startTime);
            aClass.setClassTime(classTime);
            aClass.setStartTime(startTime);
            aClass.setEndTime(endTime);
        }
        aClass.setCreatedBy(authenticationService.getName());
        aClass.setCreatedDate();
        aClass.setAttendeeType(classReq.getAttendeeDTO().getType());
        if (classReq.getTrainingProgramId() == 0) {
            aClass.setTrainingProgram(null);
        } else {
            TrainingProgram trainingProgram = trainingProgramRepository.findByIdAndStatus(classReq.getTrainingProgramId(), 1)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this training program"));
            aClass.setDuration(trainingProgram.getDuration());
            aClass.setTrainingProgram(trainingProgram);
            trainingProgram.getClasses().add(aClass);
        }
        Class newClass = classRepository.save(aClass);
        Set<ClassUser> classUserSet = new HashSet<>();
        for (ClassUserReq x : classReq.getClassUserDTOSet()) {
            Users users = usersRepository.findById(x.getUserId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this user"));
            ClassUserKey key = new ClassUserKey(users.getId(), newClass.getId());
            ClassUser classUser = new ClassUser();
            classUser.setId(key);
            classUser.setUserType(x.getUserType());
            classUser.setAClass(newClass);
            classUser.setUsers(users);
            ClassUser newClassUser = classUserRepository.save(classUser);
            classUserSet.add(newClassUser);
        }
        newClass.setClassUserType(classUserSet);
        Set<Session> sessionSet = new HashSet<>();
        if (!classReq.getListOfSessionDate().isEmpty()) {
            Collections.sort(classReq.getListOfSessionDate());
            int dayProgress = 1;
            for (LocalDate x : classReq.getListOfSessionDate()) {
                Session session = new Session();
                for (ClassUserReq y : classReq.getClassUserDTOSet()) {
                    if (y.getUserType().trim().equalsIgnoreCase("class admin")) {
                        usersRepository.findById(y.getUserId())
                                .ifPresent(user -> session.setAdminName(user.getName()));
                    }
                    if (y.getUserType().trim().equalsIgnoreCase("trainer")) {
                        usersRepository.findById(y.getUserId())
                                .ifPresent(user -> session.setTrainerName(user.getName()));
                    }
                }
                session.setClassTime(classTime);
                session.setLocation(classReq.getLocation());
                session.setSessionDate(x);
                session.setDayProgress(dayProgress++);
                session.setTotalDays(classReq.getListOfSessionDate().size());
                session.setStartTime(startTime);
                session.setEndTime(endTime);
                session.setAClass(newClass);
                Session newSession = sessionRepository.save(session);
                sessionSet.add(newSession);
            }
        }
        newClass.setSessions(sessionSet);
        newClass = classRepository.save(newClass);
        return convertToClassRes(newClass);
    }

    @Scheduled(fixedRate = 3600000)
    public void updateClassStatuses() {
        LocalDate now = LocalDate.now();
        List<Class> scheduledClasses = classRepository.findScheduledClassesBefore(now);
        for (Class classEntity : scheduledClasses) {
            if(classEntity != null) {
                classEntity.setStatus("Opening");
                classRepository.save(classEntity);
            }
        }
        List<Class> openingClasses = classRepository.findOpeningClassesAfter(now);
        for (Class classEntity : openingClasses) {
            if(classEntity != null) {
                classEntity.setStatus("Closed");
                classRepository.save(classEntity);
            }
        }
    }

    public Class updateClass(Long classId, ClassReqUpdate updatedClassReq, String status){
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Class not found"));
        if (!existingClass.getStatus().equals("Planning") && !existingClass.getStatus().equals("Scheduled")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Class cannot be updated because its status is not 'Planning' or 'Scheduled'");
        }
        String[] parts = existingClass.getCode().split("_");
        if(updatedClassReq.getLocationCode() != null) {
            if (!updatedClassReq.getLocationCode().equals(parts[0]) && !updatedClassReq.getLocationCode().isEmpty()) {
                String code = String.format("%s_%d_%02d", updatedClassReq.getLocationCode().toUpperCase(), Year.now().getValue() % 100, incrementalNumber++);
                List<Class> classList = classRepository.findAll();
                for (Class x : classList) {
                    if (x.getCode() != null) {
                        if (x.getCode().equalsIgnoreCase(code)) {
                            code = String.format("%s_%d_%02d", updatedClassReq.getLocationCode().toUpperCase(), Year.now().getValue() % 100, incrementalNumber++);
                        }
                    }
                }
                existingClass.setCode(code);
            }
        }
        existingClass.setName(updatedClassReq.getName());
        existingClass.setLocation(updatedClassReq.getLocation());
        existingClass.setFsu(updatedClassReq.getFsu());
        existingClass.setStatus(status);
        LocalTime startTime = null;
        LocalTime endTime = null;
        String classTime = null;
        if(updatedClassReq.getStartTime() != null && updatedClassReq.getEndTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            startTime = LocalTime.parse(updatedClassReq.getStartTime(), formatter);
            endTime = LocalTime.parse(updatedClassReq.getEndTime(), formatter);
            isStartTimeAndEndTimeValid(startTime, endTime);
            classTime = setClassTimeBaseOnStartTime(startTime);
        }
        existingClass.setStartTime(startTime);
        existingClass.setEndTime(endTime);
        existingClass.setClassTime(classTime);
        existingClass.setModifiedBy(authenticationService.getName());
        existingClass.setModifiedDate();
        existingClass.setAttendeeType(updatedClassReq.getAttendeeDTO().getType());
        existingClass.setPlannedAttendee(updatedClassReq.getAttendeeDTO().getPlanned());
        existingClass.setAcceptedAttendee(updatedClassReq.getAttendeeDTO().getAccepted());
        existingClass.setActualAttendee(updatedClassReq.getAttendeeDTO().getActual());
        if (existingClass.getTrainingProgram() != null) {
            if (updatedClassReq.getTrainingProgramId() == 0) {
                existingClass.getTrainingProgram().getClasses().remove(existingClass);
                existingClass.setTrainingProgram(null);
            } else if (!updatedClassReq.getTrainingProgramId().equals(existingClass.getTrainingProgram().getId())) {
                existingClass.getTrainingProgram().getClasses().remove(existingClass);
                TrainingProgram newTrainingProgram = trainingProgramRepository.findByIdAndStatus(updatedClassReq.getTrainingProgramId(), 1)
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training program not found with status Active"));
                existingClass.setDuration(newTrainingProgram.getDuration());
                existingClass.setTrainingProgram(newTrainingProgram);
            } else {
                TrainingProgram newTrainingProgram = trainingProgramRepository.findByIdAndStatus(updatedClassReq.getTrainingProgramId(), 1)
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training program not found with status Active"));
                existingClass.setDuration(newTrainingProgram.getDuration());
                existingClass.setTrainingProgram(newTrainingProgram);
            }
        }else {
            if(updatedClassReq.getTrainingProgramId() != 0) {
                TrainingProgram newTrainingProgram = trainingProgramRepository.findByIdAndStatus(updatedClassReq.getTrainingProgramId(), 1)
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training program not found with status Active"));
                existingClass.setDuration(newTrainingProgram.getDuration());
                existingClass.setTrainingProgram(newTrainingProgram);
                newTrainingProgram.getClasses().add(existingClass);
            }
        }
        Set<ClassUser> updatedClassUserSet = new HashSet<>();
        classUserRepository.deleteAllByaClass(existingClass);
        if(!updatedClassReq.getClassUserDTOSet().isEmpty()) {
            for (ClassUserReq classUserReq : updatedClassReq.getClassUserDTOSet()) {
                Users user = usersRepository.findById(classUserReq.getUserId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
                ClassUserKey key = new ClassUserKey(user.getId(), existingClass.getId());
                ClassUser classUser = new ClassUser();
                classUser.setId(key);
                classUser.setUserType(classUserReq.getUserType());
                classUser.setAClass(existingClass);
                classUser.setUsers(user);
                ClassUser newClassUser = classUserRepository.save(classUser);
                updatedClassUserSet.add(newClassUser);
            }
        }
        existingClass.setClassUserType(updatedClassUserSet);

        Set<Session> sessionSet = new HashSet<>();
        sessionRepository.deleteAllByaClass(existingClass);
        if(!updatedClassReq.getListOfSessionDate().isEmpty()) {
            Collections.sort(updatedClassReq.getListOfSessionDate());
            int dayProgress = 1;
            for (LocalDate x : updatedClassReq.getListOfSessionDate()) {
                Session session = new Session();
                for (ClassUserReq y : updatedClassReq.getClassUserDTOSet()) {
                    if (y.getUserType().trim().equalsIgnoreCase("class admin")) {
                        Users users = usersRepository.findById(y.getUserId())
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this user"));
                        session.setAdminName(users.getName());
                    }
                    if (y.getUserType().trim().equalsIgnoreCase("trainer")) {
                        Users users = usersRepository.findById(y.getUserId())
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find this user"));
                        session.setTrainerName(users.getName());
                    }
                }
                session.setLocation(updatedClassReq.getLocation());
                session.setSessionDate(x);
                session.setDayProgress(dayProgress++);
                session.setTotalDays(updatedClassReq.getListOfSessionDate().size());
                session.setStartTime(LocalTime.parse(updatedClassReq.getStartTime()));
                session.setEndTime(LocalTime.parse(updatedClassReq.getEndTime()));
                session.setAClass(existingClass);
                Session newSession = sessionRepository.save(session);
                sessionSet.add(newSession);
            }
        }
        existingClass.setSessions(sessionSet);
        Class updatedClass = classRepository.save(existingClass);
        return updatedClass;
    }

    public void validateForScheduled(ClassReqUpdate updatedClassReq) {
        if (updatedClassReq.getName() == null || updatedClassReq.getName().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (updatedClassReq.getLocation() == null || updatedClassReq.getLocation().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Location is required");
        }
        if (updatedClassReq.getLocationCode() == null || updatedClassReq.getLocationCode().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Location code is required");
        }
        if (updatedClassReq.getStartTime() == null || !isValidTime(updatedClassReq.getStartTime())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid startTime");
        }
        if (updatedClassReq.getEndTime() == null || !isValidTime(updatedClassReq.getEndTime())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid endTime");
        }
        if (updatedClassReq.getAttendeeDTO() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"AttendeeDTO is required");
        }
        if (updatedClassReq.getListOfSessionDate() == null || updatedClassReq.getListOfSessionDate().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "At least one session date is required");
        }
        if(updatedClassReq.getTrainingProgramId() == 0){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Training Program is required");
        }
    }
    private static boolean isValidTime(String time) {
        return time != null && !time.isEmpty();
    }


    @Override
    public ClassRes updateClassAsScheduled(Long classId, ClassReqUpdate updatedClassReq) {
        String status = "Scheduled";
        validateForScheduled(updatedClassReq);
        Class updatedClass = updateClass(classId, updatedClassReq, status);
        if(updatedClassReq.getListOfSessionDate().size() != updatedClass.getDuration())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Calendar dates list is not equal to Class duration (Training Program duration)");
        return convertToClassRes(updatedClass);
    }

    @Override
    public ClassRes updateClassAsPlanning(Long classId, ClassReqUpdate updatedClassReq) {
        String status = "Planning";
        Class updatedClass = updateClass(classId, updatedClassReq, status);
        return convertToClassRes(updatedClass);
    }

    public ClassRes convertToClassRes(Class newClass) {
        ClassRes classRes = new ClassRes();
        try {
            classRes.setId(newClass.getId());
            classRes.setCode(newClass.getCode());
            classRes.setStatus(newClass.getStatus());
            classRes.setName(newClass.getName());
            classRes.setLocation(newClass.getLocation());
            classRes.setFsu(newClass.getFsu());
            classRes.setStartTime(newClass.getStartTime());
            classRes.setEndTime(newClass.getEndTime());
            classRes.setCreatedBy(newClass.getCreatedBy());
            classRes.setCreatedDate(newClass.getCreatedDate());
            classRes.setModifiedBy(newClass.getModifiedBy());
            classRes.setModifiedDate(newClass.getModifiedDate());
            if(newClass.getTrainingProgram() != null)
                classRes.setTrainingProgram(trainingProgramConverter.convertToTrainingProgramRes(newClass.getTrainingProgram()));
            else classRes.setTrainingProgram(null);
            AttendeeDTO attendeeDTO = new AttendeeDTO();
            attendeeDTO.setType(newClass.getAttendeeType());
            attendeeDTO.setPlanned(newClass.getPlannedAttendee());
            attendeeDTO.setAccepted(newClass.getAcceptedAttendee());
            attendeeDTO.setActual(newClass.getActualAttendee());
            attendeeDTO.setType(newClass.getAttendeeType());
            Set<ClassUserRes> classUserDTOSet = new HashSet<>();
            if (!newClass.getClassUserType().isEmpty()){
                for (ClassUser x : newClass.getClassUserType()) {
                    ClassUserRes classUserRes = convertToClassUserRes(x);
                    classUserDTOSet.add(classUserRes);
                }
            }
            classRes.setClassUserDTOSet(classUserDTOSet);
            Set<SessionRes> sessionResSet = new HashSet<>();
            if(!newClass.getSessions().isEmpty()) {
                for (Session x : newClass.getSessions()) {
                    SessionRes sessionRes = new SessionRes();
                    sessionRes.setSessionId(x.getId());
                    sessionRes.setClassId(x.getAClass().getId());
                    sessionRes.setClassName(newClass.getName());
                    sessionRes.setClassCode(newClass.getCode());
                    sessionRes.setDayProgress(x.getDayProgress());
                    sessionRes.setTotalDays(x.getTotalDays());
                    sessionRes.setStart(x.getStartTime());
                    sessionRes.setEnd(x.getEndTime());
                    sessionRes.setClassTime(x.getClassTime());
                    sessionRes.setAdminName(x.getAdminName());
                    sessionRes.setTrainerName(x.getTrainerName());
                    sessionRes.setLocation(x.getLocation());
                    sessionRes.setSessionDate(x.getSessionDate());
                    sessionResSet.add(sessionRes);
                }
            }
            classRes.setSessionSet(sessionResSet);
            classRes.setAttendeeDTO(attendeeDTO);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return classRes;
    }
//
//    private SessionRes convertToSessionRes(Session x) {
//        SessionRes sessionRes = new SessionRes();
//        if(x.getSessionDate() != null) {
//            sessionRes.setId(x.getId());
//            sessionRes.setClassCode(x.getAClass().getCode());
//            sessionRes.setClassName(x.getAClass().getName());
//            sessionRes.setDayProgress(x.getDayProgress());
//            sessionRes.setTotalDays(x.getTotalDays());
//            sessionRes.setLocation(x.getLocation());
//            sessionRes.setClassTime(x.getClassTime());
//            sessionRes.setTrainerName(x.getTrainerName());
//            sessionRes.setAdminName(x.getAdminName());
//            sessionRes.setStart(x.getStartTime());
//            sessionRes.setEnd(x.getEndTime());
//            sessionRes.setSessionDate(x.getSessionDate());
//        }
//        return sessionRes;
//    }


    public SessionRes convertToSessionRes(Session x) {
        SessionRes sessionRes = new SessionRes();
        if (x != null && x.getSessionDate() != null && x.getAClass() != null) {
            sessionRes.setSessionId(x.getId());
            sessionRes.setClassId(x.getAClass().getId());
            sessionRes.setClassCode(x.getAClass().getCode());
            sessionRes.setClassName(x.getAClass().getName());
            sessionRes.setDayProgress(x.getDayProgress());
            sessionRes.setTotalDays(x.getTotalDays());
            sessionRes.setLocation(x.getLocation());
            sessionRes.setClassTime(x.getClassTime());
            sessionRes.setTrainerName(x.getTrainerName());
            sessionRes.setAdminName(x.getAdminName());
            sessionRes.setFsu(x.getAClass().getFsu());
            sessionRes.setStart(x.getStartTime());
            sessionRes.setEnd(x.getEndTime());
            sessionRes.setSessionDate(x.getSessionDate());
        }
        return sessionRes;
    }


    private ClassUserRes convertToClassUserRes(ClassUser classUser) {
        ClassUserRes classUserRes = new ClassUserRes();
        if (classUser.getUsers() != null) {
            classUserRes.setUserId(classUser.getUsers().getId()); // Only set if not null
            classUserRes.setName(classUser.getUsers().getName()); // Assuming you want to check for null separately
            classUserRes.setUserType(classUser.getUserType());
        }
        return classUserRes;
    }

    public String setClassTimeBaseOnStartTime(LocalTime startTime){
        if(startTime.isAfter(LocalTime.of(7, 59)) && startTime.isBefore(LocalTime.of(12, 1))) return "Morning";
        else if(startTime.isAfter(LocalTime.of(12, 59)) && startTime.isBefore(LocalTime.of(17, 1))) return "Noon";
        else if(startTime.isAfter(LocalTime.of(17, 59)) && startTime.isBefore(LocalTime.of(22, 1))) return "Night";
        else throw new ApiException(HttpStatus.BAD_REQUEST, "Start time is not in any particular period");
    }

    public void isStartTimeAndEndTimeValid(LocalTime startTime, LocalTime endTime){
        // Morning period check
        if (startTime.isAfter(LocalTime.of(7, 59)) && startTime.isBefore(LocalTime.of(12, 1))) {
            if (!(endTime.isAfter(LocalTime.of(7, 59)) && endTime.isBefore(LocalTime.of(12, 1)))) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "End time must be in the Morning period as well");
            }
        }
        // Noon period check
        else if (startTime.isAfter(LocalTime.of(12, 59)) && startTime.isBefore(LocalTime.of(17, 1))) {
            if (!(endTime.isAfter(LocalTime.of(12, 59)) && endTime.isBefore(LocalTime.of(17, 1)))) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "End time must be in the Noon period as well");
            }
        }
        // Night period check
        else if (startTime.isAfter(LocalTime.of(17, 59)) && startTime.isBefore(LocalTime.of(22, 1))) {
            if (!(endTime.isAfter(LocalTime.of(17, 59)) && endTime.isBefore(LocalTime.of(22, 1)))) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "End time must be in the Night period as well");
            }
        }
        else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be within defined periods (Morning, Noon, Night)");
        }
    }
}
