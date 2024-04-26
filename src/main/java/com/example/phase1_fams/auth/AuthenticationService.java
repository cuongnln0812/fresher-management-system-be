package com.example.phase1_fams.auth;

import com.example.phase1_fams.dto.response.StatisticsRes;
import com.example.phase1_fams.dto.response.UserAuthRes;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.model.Users;
import com.example.phase1_fams.repository.ClassRepository;
import com.example.phase1_fams.repository.SyllabusRepository;
import com.example.phase1_fams.repository.TrainingProgramRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.security.JwtService;


@Service
public class AuthenticationService {

        private final UsersRepository repository;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UsersRepository usersRepository;
        private final SyllabusRepository syllabusRepository;
        private final TrainingProgramRepository trainingProgramRepository;
        private final ClassRepository classRepository;
        private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UsersRepository repository, PasswordEncoder passwordEncoder,
                                 JwtService jwtService, AuthenticationManager authenticationManager,
                                 UsersRepository usersRepository, SyllabusRepository syllabusRepository,
                                 TrainingProgramRepository trainingProgramRepository, ClassRepository classRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.syllabusRepository = syllabusRepository;
        this.trainingProgramRepository = trainingProgramRepository;
        this.classRepository = classRepository;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User cannot found!"));
                if(!user.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "User is forbidden!");
                var jwtToken = jwtService.generateToken(user);
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setToken(jwtToken);
                return authenticationResponse;
        }

    public UserAuthRes getUserInfo(){
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = repository.findByEmail(name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User cannot found!"));
            return UserAuthRes.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .dob(user.getDob())
                    .phone(user.getPhone())
                    .roleName(user.getRole().getRoleName())
                    .gender(user.getGender())
                    .syllabusPermission(String.valueOf(user.getRole().getSyllabusPermissionGroup()))
                    .trainingProgramPermission(String.valueOf(user.getRole().getTrainingProgramPermissionGroup()))
                    .classPermission(String.valueOf(user.getRole().getClassPermissionGroup()))
                    .learningMaterialPermission(String.valueOf(user.getRole().getLearningMaterialPermissionGroup()))
                    .isFirstLogin(user.isFirstLogin())
                    .build();
    }

    public String getName() {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users users = repository.findByEmail(email)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find who is logged in"));
            return users.getName();
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = repository.findByEmail(username)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User cannot found!"));
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Old password does not match!");
            }
            if(!newPassword.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$"))
                throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Password must be 8-12 characters with at least one uppercase letter, one number, and one special character (!@#$%^&*).");
            user.setPassword(passwordEncoder.encode(newPassword));
            if(user.isFirstLogin()) user.setFirstLogin(false);
            repository.save(user);
    }

    public StatisticsRes getAllStatistics(){
        Integer totalNumOfUserInactive = usersRepository.countAllByStatus(false).orElse(0);
        Integer totalNumOfUserActive = usersRepository.countAllByStatus(true).orElse(0);
        Integer totalNumOfUser = totalNumOfUserInactive + totalNumOfUserActive;

        Integer totalNumOfSyllabusInactive = syllabusRepository.countAllByStatus(0).orElse(0);
        Integer totalNumOfSyllabusDraft = syllabusRepository.countAllByStatus(2).orElse(0);
        Integer totalNumOfSyllabusActive = syllabusRepository.countAllByStatus(1).orElse(0);
        Integer totalNumOfSyllabus = totalNumOfSyllabusActive + totalNumOfSyllabusInactive + totalNumOfSyllabusDraft;

        Integer totalNumOfTrainingProgramInactive = trainingProgramRepository.countAllByStatus(0).orElse(0);
        Integer totalNumOfTrainingProgramDraft = trainingProgramRepository.countAllByStatus(2).orElse(0);
        Integer totalNumOfTrainingProgramActive = trainingProgramRepository.countAllByStatus(1).orElse(0);
        Integer totalNumOfTrainingProgram = totalNumOfTrainingProgramInactive + totalNumOfTrainingProgramDraft + totalNumOfTrainingProgramActive;

        Integer totalNumOfClassPlanning = classRepository.countAllByStatus("Planning").orElse(0);
        Integer totalNumOfClassScheduled = classRepository.countAllByStatus("Scheduled").orElse(0);
        Integer totalNumOfClassOpening = classRepository.countAllByStatus("Opening").orElse(0);
        Integer totalNumOfClassClosed = classRepository.countAllByStatus("Closed").orElse(0);
        Integer totalNumOfClassInactive = classRepository.countAllByStatus("Inactive").orElse(0);
        Integer totalNumOfClass = totalNumOfClassPlanning + totalNumOfClassScheduled + totalNumOfClassOpening + totalNumOfClassClosed + totalNumOfClassInactive;

        return new StatisticsRes(totalNumOfUser, totalNumOfUserActive, totalNumOfUserInactive,
                totalNumOfSyllabus, totalNumOfSyllabusActive, totalNumOfSyllabusDraft, totalNumOfSyllabusInactive,
                totalNumOfTrainingProgram, totalNumOfTrainingProgramActive, totalNumOfTrainingProgramDraft, totalNumOfTrainingProgramInactive,
                totalNumOfClass, totalNumOfClassPlanning, totalNumOfClassScheduled, totalNumOfClassOpening, totalNumOfClassClosed, totalNumOfClassInactive);
    }
}
