package com.example.phase1_fams.controller;


import com.example.phase1_fams.dto.response.MaterialRes;
import com.example.phase1_fams.service.LearningMaterialService;
import com.example.phase1_fams.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/training-contents/{trainingContentId}/learning-materials")
@Tag(name = "REST APIs for Learning Material Management")
@SecurityRequirement(name = "bearerAuth")
public class LearningMaterialController {
    private LearningMaterialService learningMaterialService;

    @Autowired
    public LearningMaterialController(LearningMaterialService learningMaterialService) {
        this.learningMaterialService = learningMaterialService;
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('learning_material:upload')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(@PathVariable("trainingContentId") Long trainingContentId,
                                        @RequestParam(value = "file", required = false) List<MultipartFile> files,
                                        @RequestParam(value = "deletedMaterialIds", required = false) List<Long> deletedMaterialIds)
    {
        MaterialRes res = learningMaterialService.saveFiles(files, deletedMaterialIds, trainingContentId);
        return ResponseUtils.response(res, "Upload file successfully", HttpStatus.OK);
    }
//
//
//    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
//    @PreAuthorize("hasAuthority('learning_material:download')")
//    @GetMapping("/download/{fileName}")
//    public ResponseEntity<String> downloadUrl(@PathVariable("trainingContentId") Long trainingContentId, @PathVariable("fileName") String fileName) {
//        String url = learningMaterialService.generateUrl (fileName, HttpMethod.GET, trainingContentId);
//        return ResponseEntity.ok(url);
//    }
}
