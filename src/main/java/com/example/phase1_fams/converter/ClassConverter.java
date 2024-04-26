package com.example.phase1_fams.converter;

import java.time.LocalDate;
import java.util.*;

import com.example.phase1_fams.dto.AttendeeDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.dto.ClassGeneralDTO;
import com.example.phase1_fams.dto.response.ClassDetailsRes;
import com.example.phase1_fams.model.Class;
import com.example.phase1_fams.model.Session;

@Component
public class ClassConverter {

    private final TrainingProgramConverter trainingProgramConverter;

    @Autowired
    public ClassConverter(TrainingProgramConverter trainingProgramConverter){
        this.trainingProgramConverter = trainingProgramConverter;
    }

    public ClassDTO convertToPageRes(Class class1) {
        ClassDTO classDTO = new ClassDTO();
        classDTO.setId(class1.getId());
        classDTO.setCode(class1.getCode());
        classDTO.setName(class1.getName());
        classDTO.setCreatedDate(class1.getCreatedDate());
        classDTO.setCreatedBy(class1.getCreatedBy());
        classDTO.setDuration(class1.getDuration());
        classDTO.setStatus(class1.getStatus());
        classDTO.setLocation(class1.getLocation());
        classDTO.setFsu(class1.getFsu());
        return classDTO;
    }

    public ClassDetailsRes convertToDetailsRes(Class aClass) {
        try {
            ClassDetailsRes classDetailsRes = new ClassDetailsRes();
            classDetailsRes.setClassId(aClass.getId());
            classDetailsRes.setClassCode(aClass.getCode());
            classDetailsRes.setClassName(aClass.getName());
            classDetailsRes.setStatus(aClass.getStatus());
            classDetailsRes.setCreatedBy(aClass.getCreatedBy());
            classDetailsRes.setCreatedDate(aClass.getCreatedDate());
            classDetailsRes.setModifiedBy(aClass.getModifiedBy());
            classDetailsRes.setModifiedDate(aClass.getModifiedDate());
            AttendeeDTO attendeeDTO = new AttendeeDTO();
            attendeeDTO.setType(aClass.getAttendeeType());
            attendeeDTO.setAccepted(aClass.getAcceptedAttendee());
            attendeeDTO.setActual(aClass.getActualAttendee());
            attendeeDTO.setPlanned(aClass.getPlannedAttendee());
            classDetailsRes.setAttendeeDTO(attendeeDTO);
            if (aClass.getTrainingProgram() != null) classDetailsRes.setTrainingProgramRes(trainingProgramConverter
                    .convertToTrainingProgramRes(aClass.getTrainingProgram()));
            else classDetailsRes.setTrainingProgramRes(null);
            ClassGeneralDTO classGeneralDTO = new ClassGeneralDTO();
            classDetailsRes.setClassGeneralDTO(classGeneralDTO);
            //Map admin
            Optional<String> adminNameOptional = aClass.getClassUserType().stream()
                    .filter(classUser -> classUser.getUserType().trim().equalsIgnoreCase("class admin"))
                    .map(classUser -> classUser.getUsers().getName())
                    .findFirst();
            String adminName = adminNameOptional.orElse("No Admin Found");
            classDetailsRes.getClassGeneralDTO().setAdmin(adminName);
            Optional<Long> adminIdOptional = aClass.getClassUserType().stream()
                    .filter(classUser -> classUser.getUserType().trim().equalsIgnoreCase("class admin"))
                    .map(classUser -> classUser.getUsers().getId())
                    .findFirst();
            Long adminId = adminIdOptional.orElse(0L);
            classDetailsRes.getClassGeneralDTO().setAdminId(adminId);
            //Map trainer
            Optional<String> trainerNameOptional = aClass.getClassUserType().stream()
                    .filter(classUser -> classUser.getUserType().trim().equalsIgnoreCase("trainer"))
                    .map(classUser -> classUser.getUsers().getName())
                    .findFirst();
            String trainerName = trainerNameOptional.orElse("No Trainer Found");
            classDetailsRes.getClassGeneralDTO().setTrainer(trainerName);
            Optional<Long> trainerIdOptional = aClass.getClassUserType().stream()
                    .filter(classUser -> classUser.getUserType().trim().equalsIgnoreCase("trainer"))
                    .map(classUser -> classUser.getUsers().getId())
                    .findFirst();
            Long trainerId = trainerIdOptional.orElse(0L);
            classDetailsRes.getClassGeneralDTO().setTrainerId(trainerId);

            // Now you can set properties of classGeneralDTO
            classDetailsRes.getClassGeneralDTO().setFsu(aClass.getFsu());
            classDetailsRes.getClassGeneralDTO().setLocation(aClass.getLocation());
            classDetailsRes.getClassGeneralDTO().setClassStartTime(aClass.getStartTime());
            classDetailsRes.getClassGeneralDTO().setClassEndTime(aClass.getEndTime());
            // classDetailsRes.getClassGeneralDTO().setSessionSet(aClass.getSessions());
            Set<Session> sessions = aClass.getSessions();
            List<LocalDate> calendarDates = new ArrayList<>();
            for (Session session : sessions) {
                if(session.getSessionDate() != null) calendarDates.add(session.getSessionDate());
            }
            classDetailsRes.setCalendarDates(calendarDates);
            classDetailsRes.getCalendarDates().sort(Comparator.naturalOrder());
            return classDetailsRes;
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
