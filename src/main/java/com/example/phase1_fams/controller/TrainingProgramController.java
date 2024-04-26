package com.example.phase1_fams.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.example.phase1_fams.dto.ClassDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.phase1_fams.dto.TrainingProgramDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.TrainingProgramReq;
import com.example.phase1_fams.dto.request.TrainingProgramReqUpdate;
import com.example.phase1_fams.dto.response.TrainingProgramRes;
import com.example.phase1_fams.service.TrainingProgramService;
import com.example.phase1_fams.utils.AppConstants;
import com.example.phase1_fams.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/training-program")
@Tag(name = "REST APIs for Training Program Management")
@SecurityRequirement(name = "bearerAuth")
public class TrainingProgramController {
    private final TrainingProgramService trainingProgramService;

    @Autowired
    public TrainingProgramController(TrainingProgramService trainingProgramService) {
        this.trainingProgramService = trainingProgramService;
    }

    @PreAuthorize("hasAuthority('training_program:read')")
    @Operation(summary = "View training program details", description = "View training program details by training program id.  Nhớ truyền xuống id của training program muốn tìm ")
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping("/{id}")
    public ResponseEntity<?> viewTrainingProgramDetails(@PathVariable(name = "id") Long id) {
        TrainingProgramRes trainingProgramRes = trainingProgramService.getTrainingProgramDetails(id);
        return ResponseUtils.response(trainingProgramRes, "Get training program details successfully!", HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('class:create', 'class:update', 'training_program:read')")
    @Operation(summary = "Get active training program list by name", description = "Get active training program list for creating and updating classes")
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping("/active")
    public ResponseEntity<?> getActiveTrainingProgramList(@RequestParam(required = false) String trainingProgramName) {
        List<TrainingProgramDTO> trainingProgramRes = trainingProgramService.getActiveTrainingProgramList(trainingProgramName);
        return ResponseUtils.response(trainingProgramRes, "Get active training program list successfully!", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('training_program:import')")
    @GetMapping("/template")
    public ResponseEntity<InputStreamResource> downloadTrainingProgramImportTemplate() {

        String googleDriveLink = "https://drive.google.com/uc?export=download&id=1euefC_Tdg3vmG0wbgi1IAAV9xLpdXP2U";

        InputStreamResource resource = trainingProgramService.downloadFileFromGoogleDrive(googleDriveLink);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"TrainingProgramTemplate.xlsx\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);

    }

    @Operation(summary = "Create training program as active", description = "Create training program in status Active (status: 1), required validation")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('training_program:create')")
    @PostMapping("")
    public ResponseEntity<?> createTrainingProgramAsActive(@RequestBody @Valid TrainingProgramReq trainingProgramReq) {
        TrainingProgramRes trainingProgramDTO = trainingProgramService
                .createTrainingProgramAsActive(trainingProgramReq);
        return ResponseUtils.response(trainingProgramDTO, "Training program created successfully!", HttpStatus.CREATED);
    }

    @Operation(summary = "Create training program as draft", description = "Create training program in status draft (status: 2), no validation needed")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('training_program:create')")
    @PostMapping("/draft")
    public ResponseEntity<?> createTrainingProgramAsDraft(@RequestBody @Valid TrainingProgramReq trainingProgramReq) {
        TrainingProgramRes trainingProgramDTO = trainingProgramService.createTrainingProgramAsDraft(trainingProgramReq);
        return ResponseUtils.response(trainingProgramDTO, "Draft training program created successfully!",
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update training program as active", description = "Update training program in status active (status: 1), required validation")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('training_program:update')")
    @PutMapping("")
    public ResponseEntity<?> updateTrainingProgramAsActive(
            @RequestBody @Valid TrainingProgramReqUpdate trainingProgramReqUpdate) {
        TrainingProgramRes trainingProgramDTO = trainingProgramService
                .updateTrainingProgramAsActive(trainingProgramReqUpdate);
        return ResponseUtils.response(trainingProgramDTO, "Training program update successfully!", HttpStatus.OK);
    }

    @Operation(summary = "Update training program as draft", description = "Update training program in status draft (status: 2), no validation needed")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('training_program:update')")
    @PutMapping("/draft")
    public ResponseEntity<?> updateTrainingProgramAsDraft(
            @RequestBody @Valid TrainingProgramReqUpdate trainingProgramReqUpdate) {
        TrainingProgramRes trainingProgramDTO = trainingProgramService
                .updateTrainingProgramAsDraft(trainingProgramReqUpdate);
        return ResponseUtils.response(trainingProgramDTO, "Training program update successfully!", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('training_program:import')")
    @PostMapping(value = "/import/{duplicateOption}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file,
            @PathVariable Integer duplicateOption) {
        try {
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")
                    && !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported file format");
            }

            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                trainingProgramService.importTrainingProgram(file, duplicateOption);
            } else if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
                trainingProgramService.importTrainingProgramCSV(file, duplicateOption);
            }

            String successMessage = "Training Program imported successfully!";
            if (duplicateOption.equals(0)) {
                successMessage += " (skipped)";
            }
            return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
        } catch (ApiException e) {
            return new ResponseEntity<>("Error importing training program: " + e.getMessage(), e.getStatus());
        }
    }

    @Operation(summary = "Duplicate training-programs", description = "Duplicate training-programs in status draft (status: 2), get by training-programs code")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('training_program:create')")
    @PostMapping("/duplicate/{id}")
    public ResponseEntity<?> duplicateTrainingProgram(@PathVariable(name = "id") Long id) {
        TrainingProgramRes trainingProgramRes = trainingProgramService.duplicateTrainingProgram(id);
        return ResponseUtils.response(trainingProgramRes, "Duplicated Training Program successfully",
                HttpStatus.CREATED);
    }

    @Operation(summary = "De-active/Activate training-programs", description = "De-active/Activate training-programs in either status active or inactive, if status is draft then it's unable to de-active/activate")
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @PreAuthorize("hasAuthority('training_program:delete')")
    @PutMapping("/{id}")
    public ResponseEntity<?> switchStatus(@PathVariable(name = "id") Long id) {
        TrainingProgramRes res = trainingProgramService.switchStatus(id);
        return ResponseUtils.response(res, "Change Training Program status successfully", HttpStatus.OK);
    }

    @Operation(summary = "Search or Get all training programs", description = "Get all training program by page")
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping("")
    @PreAuthorize("hasAuthority('training_program:read')")
    public ResponseEntity<?> searchTrainingPrograms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> createdBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<TrainingProgramDTO> trainingPrograms = trainingProgramService.searchTrainingPrograms(keyword, createdBy, startDate,
                                                                                        endDate, duration, statuses, pageRequest);
        return ResponseUtils.response(trainingPrograms, "Get Training Program page successfully", HttpStatus.OK);
    }

    @Operation(summary = "Get class list by training program", description = "Get class list by training program id with pagination")
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping("/{id}/classes")
    @PreAuthorize("hasAuthority('training_program:read')")
    public ResponseEntity<?> searchTrainingPrograms(
            @PathVariable Long id,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size
    ) {
        Page<ClassDTO> classDTOPage = trainingProgramService.getClassListByTrainingProgram(id, page, size);
        return ResponseUtils.response(classDTOPage, "Get Class page by Training program successfully", HttpStatus.OK);
    }
}
