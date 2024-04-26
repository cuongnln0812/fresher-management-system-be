package com.example.phase1_fams.controller;

import java.time.LocalDate;
import java.util.List;

import com.example.phase1_fams.dto.request.ClassReqUpdate;
import com.example.phase1_fams.dto.response.SessionRes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

import com.example.phase1_fams.dto.ClassDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.ClassReq;
import com.example.phase1_fams.dto.request.SessionReq;
import com.example.phase1_fams.dto.response.ClassDetailsRes;
import com.example.phase1_fams.dto.response.ClassRes;
import com.example.phase1_fams.service.ClassService;
import com.example.phase1_fams.service.SessionService;
import com.example.phase1_fams.utils.AppConstants;
import com.example.phase1_fams.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/class")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "REST APIs for Class Management")
public class ClassController {
    private final ClassService classService;
    private final SessionService sessionService;

    @Autowired
    public ClassController(ClassService classService, SessionService sessionService) {
        this.classService = classService;
        this.sessionService = sessionService;
    }

    @Operation(summary = "Deactivate class", description = "Deactivate class by code . Change the status to Deactive")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('class:delete')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateClass(@PathVariable(value = "id") Long classID) {
        try {
            classService.deactivateClass(classID);
            return new ResponseEntity<>("Deactivate successfully!", HttpStatus.ACCEPTED);
        } catch (ApiException e) {
            return ResponseUtils.error(e);
        }
    }

    @Operation(summary = "Get all or filter class", description = "Get all if no param or filter class with ")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('class:read')")
    @GetMapping("")
    public ResponseEntity<?> getAllClassesOrFilter(
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) List<String> location,
            @RequestParam(required = false) List<String> attendeeType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) List<String> time,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> fsu,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {

        Page<ClassDTO> classes = classService.findFilteredClasses(searchKey, location, attendeeType, fromDate, toDate, time, status, fsu, page, size);
        return ResponseUtils.response(classes, "Get or filter class success!", HttpStatus.OK);

    }

    @Operation(summary = "Get class details", description = "Get class details by class id")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('class:read')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClassDetails(@PathVariable(value = "id") Long classID) {
        try {
            ClassDetailsRes classDetailsRes = classService.getClassDetails(classID);
            return ResponseUtils.response(classDetailsRes, "Get class details successfully", HttpStatus.OK);
        } catch (ApiException e) {
            return ResponseUtils.error(e);
        }
    }

    @Operation(summary = "View training calendar", description = "View all training session of all class with status ")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('class:read')")
    @GetMapping("/calendar")
    public ResponseEntity<?> getTrainingCalendar(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> location,
            @RequestParam(required = false) List<String> classTime,
            @RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> fsu,
            @RequestParam(required = false) List<String> trainer) {
        return ResponseUtils.response(sessionService.getOrFilterSession(keyword, location, classTime,
                                                                        startDate, endDate, status, fsu,
                                                                        trainer),
                "Get training calendar successful", HttpStatus.OK);
    }

    @Operation(summary = "Create class as scheduled", description = "Create class in status Scheduled (String status: Scheduled), required validation" +
            "FSU: \"F-Town 1\",\"F-Town 2\", \"F-Town 3\" ")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('class:create')")
    @PostMapping("")
    public ResponseEntity<?> createClassAsScheduled(@RequestBody @Valid ClassReq classReq) {
        ClassRes classRes = classService.createClassAsScheduled(classReq);
        return ResponseUtils.response(classRes, "Class created successfully!", HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create class as planning",
            description = "Create class in status Planning (String status: Planning), no validation needed. " +
                    "FSU: \"F-Town 1\",\"F-Town 2\", \"F-Town 3\" "
    )
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('class:create')")
    @PostMapping("/draft")
    public ResponseEntity<?> createClassAsPlanning(@RequestBody @Valid ClassReq classReq) {
        ClassRes classRes = classService.createClassAsPlanning(classReq);
        return ResponseUtils.response(classRes, "Planning class created successfully!", HttpStatus.CREATED);
    }
//    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
//    @PutMapping("/calendar/{sessionId}/")
//    @PreAuthorize("hasAuthority('class:update')")
//    public ResponseEntity<?> updateSessionInClass(
//                                                  @PathVariable("sessionId") Long sessionId,
//                                                  @RequestBody SessionReq sessionReq) {
//        try {
//            classService.updateSessionInClass(sessionId, sessionReq);
//            return ResponseEntity.ok("Session updated successfully!");
//        }catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating session: " + e.getMessage());
//        }
//
//    }

    @Operation(
            summary = "Update class as scheduled",
            description = "Update class when status Scheduled, required validation"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @PreAuthorize("hasAuthority('class:update')")
    @PutMapping("/scheduled/{classId}/")
    public ResponseEntity<?> updateClassAsScheduled(@PathVariable Long classId, @RequestBody ClassReqUpdate updatedClassReq) {
        ClassRes classRes = classService.updateClassAsScheduled(classId, updatedClassReq);
        return ResponseUtils.response(classRes, "Class updated to successfully!", HttpStatus.OK);
    }

    @Operation(
            summary = "Update class as planning",
            description = "Update class when status Planning, required validation"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @PreAuthorize("hasAuthority('class:update')")
    @PutMapping("/planning/{classId}/")
    public ResponseEntity<?> updateClassAsPlanning(@PathVariable Long classId, @RequestBody ClassReqUpdate updatedClassReq) {
        ClassRes classRes = classService.updateClassAsPlanning(classId, updatedClassReq);
        return ResponseUtils.response(classRes, "Class updated to successfully!", HttpStatus.OK);
    }

//    @Operation(
//            summary = "Update class calendar",
//            description = "Update each session of class"
//    )
//    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
//    @PutMapping("/calendar/{sessionId}/")
//    @PreAuthorize("hasAuthority('class:update')")
//    public ResponseEntity<?> updateSessionInClass(
//                                                  @PathVariable("sessionId") Long sessionId,
//                                                  @RequestBody SessionReq sessionReq) {
//        try {
//            SessionRes res = classService.updateSessionInClass(sessionId, sessionReq);
//            return ResponseUtils.response(res, "Session updated successfully!", HttpStatus.OK);
//        }catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating session: " + e.getMessage());
//        }
//    }

    @Operation(
            summary = "Update class calendar ",
            description = "Update all session of class"
    )
    @PutMapping("/calendar/{classId}")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('class:update')")
    public ResponseEntity<?> updateAllSessionsInClass(@PathVariable("classId") Long classId,
                                                      @RequestBody SessionReq sessionReq) {
        List<SessionRes> updatedSessions = classService.updateAllSessionsInClass(classId, sessionReq);
        return ResponseUtils.response(updatedSessions,"All sessions in the class updated successfully!", HttpStatus.OK);
    }

}
