package com.example.phase1_fams.controller;

import java.time.LocalDate;
import java.util.List;

import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.ActiveSyllabus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.phase1_fams.dto.request.SyllabusReq;
import com.example.phase1_fams.dto.request.SyllabusReqUpdate;
import com.example.phase1_fams.dto.response.SyllabusDetailsRes;
import com.example.phase1_fams.dto.response.SyllabusPageRes;
import com.example.phase1_fams.service.SyllabusService;
import com.example.phase1_fams.utils.AppConstants;
import com.example.phase1_fams.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/syllabus")
@Tag(name = "REST APIs for Syllabus Management")
@SecurityRequirement(name = "bearerAuth")
public class SyllabusController {

    private final SyllabusService syllabusService;

    @Autowired
    public SyllabusController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    @Operation(
            summary = "Import syllabus",
            description = "Import syllabus template excel file, use the file from the download function"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @PreAuthorize("hasAuthority('syllabus:import')")
    @PostMapping(value = "/import/{duplicateOption}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file,
            @PathVariable Integer duplicateOption) {
        try {
            if (!duplicateOption.equals(0)) {
                syllabusService.processImportedFile2(file, duplicateOption);
                return new ResponseEntity<>("Syllabus imported successfully!", HttpStatus.CREATED);
            } else {
                syllabusService.processImportedFile2(file, duplicateOption);
                return new ResponseEntity<>("Syllabus imported successfully!(skipped)", HttpStatus.CREATED);
            }
        } catch (ApiException e) {
            return new ResponseEntity<>("Error importing syllabus: " + e.getMessage(), e.getStatus());
        }
    }

    @Operation(
            summary = "Download syllabus template",
            description = "Download syllabus template excel file for the import function"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @PreAuthorize("hasAuthority('syllabus:import')")
    @GetMapping("/template")
    public ResponseEntity<InputStreamResource> downloadSyllabusImportTemplate(){
        String googleDriveLink = "https://drive.google.com/uc?export=download&id=19TEkYDxe63vQb0lAX7BR2MQ3JZ0Mr011";
        InputStreamResource resource = syllabusService.downloadFileFromGoogleDrive(googleDriveLink);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"SyllabusTemplate.xlsx\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @Operation(summary = "Create syllabus as active", description = "Create syllabus in status Active (status: 1), required validation")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('syllabus:create')")
    @PostMapping("")
    public ResponseEntity<?> createSyllabusAsActive(@RequestBody @Valid SyllabusReq syllabusReq) {
        SyllabusDetailsRes detailsRes = syllabusService.createSyllabusAsActive(syllabusReq);
        return ResponseUtils.response(detailsRes, "Syllabus created successfully!", HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create syllabus as draft",
            description = "Create syllabus in status draft (status: 2), no validation needed"
    )
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('syllabus:create')")
    @PostMapping("/draft")
    public ResponseEntity<?> createSyllabusAsDraft(@RequestBody @Valid SyllabusReq syllabusReq) {
        SyllabusDetailsRes detailsRes = syllabusService.createSyllabusAsDraft(syllabusReq);
        return ResponseUtils.response(detailsRes, "Draft syllabus created successfully!", HttpStatus.CREATED);
    }

    @Operation(
            summary = "Duplicate syllabus",
            description = "Duplicate syllabus in status draft (status: 2), get by syllabus code"
    )
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PreAuthorize("hasAuthority('syllabus:create')")
    @PostMapping("/duplicate/{code}")
    public ResponseEntity<?> duplicateSyllabus(@PathVariable(name = "code") String code) {
        SyllabusDetailsRes detailsRes = syllabusService.duplicateSyllabus(code);
        return ResponseUtils.response(detailsRes, "Duplicated syllabus created successfully", HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('syllabus:read')")
    @Operation(
            summary = "Get all syllabus",
            description = "Get all syllabus with paging. Truyền vô page number (default = 0) và page size (default = 10)"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("")
    public ResponseEntity<?> getAllUserPageOrSearch(@RequestParam(required = false) String searchKey,
                                            @RequestParam(required = false) List<String> createdBy,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                            @RequestParam(required = false) Integer duration,
                                            @RequestParam(required = false) List<String> outputStandards,
                                            @RequestParam(required = false) List<Integer> statuses,
                                            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
                                            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {

        Page<SyllabusPageRes> syllabusPage = syllabusService.searchSyllabus(searchKey, createdBy, startDate, endDate, duration, outputStandards, statuses, page, size);
        return ResponseUtils.response(syllabusPage, "Retrieved syllabus page successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('training_program:create', 'training_program:update', 'syllabus:read')")
    @Operation(
            summary = "Get active syllabuses",
            description = "Get all active syllabus for creating training program"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("active")
    public ResponseEntity<?> getActiveSyllabusList(@RequestParam(required = false) String syllabusName) {
        List<ActiveSyllabus> syllabusPage = syllabusService.getActiveSyllabusList(syllabusName);
        return ResponseUtils.response(syllabusPage, "Get active syllabus list successfully", HttpStatus.OK);
    }

    @Operation(
            summary = "Get syllabus details",
            description = "Get syllabus details by syllabus code"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('syllabus:read')")
    @GetMapping("/{code}")
    public ResponseEntity<?> getSyllabusDetails(@PathVariable String code) {
        SyllabusDetailsRes detailsRes = syllabusService.getSyllabusDetails(code);
        return ResponseUtils.response(detailsRes, "Get syllabus details successfully", HttpStatus.OK);
    }

    @Operation(
            summary = "Update syllabus as active",
            description = "Update syllabus in status active (status: 1), required validation"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('syllabus:update')")
    @PutMapping("")
    public ResponseEntity<?> updateSyllabusAsActive(@RequestBody @Valid SyllabusReqUpdate syllabusReq) {
        SyllabusDetailsRes detailsRes = syllabusService.updateSyllabusAsActive(syllabusReq);
        return ResponseUtils.response(detailsRes, "Active syllabus updated successfully!", HttpStatus.OK);
    }

    @Operation(
            summary = "Update syllabus as draft",
            description = "Update syllabus in status draft (status: 2), no validation needed"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('syllabus:update')")
    @PutMapping("/draft")
    public ResponseEntity<?> updateSyllabusAsDraft(@RequestBody @Valid SyllabusReqUpdate syllabusReq) {
        SyllabusDetailsRes detailsRes = syllabusService.updateSyllabusAsDraft(syllabusReq);
        return ResponseUtils.response(detailsRes, "Draft syllabus update successfully!", HttpStatus.OK);
    }

    @Operation(
            summary = "Delete syllabus (soft delete)",
            description = "Delete syllabus by code (soft delete). Delete by change the status to Inactive (status: 0)"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('syllabus:delete')")
    @DeleteMapping("/{code}")
    public ResponseEntity<?> deactivateSyllabus(@PathVariable(value = "code") String code) {
        SyllabusDetailsRes deleted = syllabusService.deactiveSyllabus(code); // Deactivate the syllabus instead of deleting it
        return ResponseUtils.response(deleted, "Delete syllabus successfully (soft delete)", HttpStatus.OK);
    }
}
