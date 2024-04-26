package com.example.phase1_fams.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.MaterialRes;
import com.example.phase1_fams.model.LearningMaterial;
import com.example.phase1_fams.model.TrainingContent;
import com.example.phase1_fams.model.Users;
import com.example.phase1_fams.repository.LearningMaterialRepository;
import com.example.phase1_fams.repository.TrainingContentRepository;
import com.example.phase1_fams.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.phase1_fams.service.LearningMaterialService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
@Transactional
public class LearningMaterialServiceImpl implements LearningMaterialService {

    @Value("${aws.s3.bucketName}")
    private String bucketName;
    private final AmazonS3 s3;
    private final UsersRepository usersRepository;
    private final TrainingContentRepository trainingContentRepository;
    private final LearningMaterialRepository learningMaterialRepository;

    @Autowired
    public LearningMaterialServiceImpl(AmazonS3 s3, UsersRepository usersRepository, TrainingContentRepository trainingContentRepository, LearningMaterialRepository learningMaterialRepository) {
        this.s3 = s3;
        this.usersRepository = usersRepository;
        this.trainingContentRepository = trainingContentRepository;
        this.learningMaterialRepository = learningMaterialRepository;
    }

    @Override
    public MaterialRes saveFiles(List<MultipartFile> files, List<Long> deletedMaterialIds, Long trainingContentId){
        TrainingContent trainingContent = trainingContentRepository.findById(trainingContentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No Training Content found to add material!"));

        List<String> savedFileNames = new ArrayList<>();
        List<Map<String, String>> listOfMaterialDeleted = new ArrayList<>(); // Note the change to Map<String, String> for clarity

        if(deletedMaterialIds != null && !deletedMaterialIds.isEmpty()) {
            for (Long id : deletedMaterialIds) {
                learningMaterialRepository.findById(id).ifPresent(learningMaterial -> {
                    Map<String, String> deletedMaterialInfo = new HashMap<>();
                    // Assuming you have a method in learningMaterial to get the file name. Adjust accordingly.
                    deletedMaterialInfo.put("id", id.toString()); // Convert ID to String for consistency in Map
                    deletedMaterialInfo.put("fileName", learningMaterial.getFileName() + learningMaterial.getFileType()); // Adjust getFileName() based on your method
                    listOfMaterialDeleted.add(deletedMaterialInfo); // Add to your list
                    // Now remove and delete the learning material
                    trainingContent.getLearningMaterials().remove(learningMaterial);
                    learningMaterialRepository.delete(learningMaterial);
                });
            }
        }

        if(files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file.getSize() > 25 * 1024 * 1024) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Files size exceed the maximum limit of 25MB.");
                }

                String originalFileName = file.getOriginalFilename();
                String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "pdf", "ppt", "pptx", "mp4", "avi", "mov", "xls", "xlsx", "docx", ".rar", ".zip"};
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
                if (!Arrays.asList(allowedExtensions).contains(fileExtension.toLowerCase())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported file type. Allowed types are: " + Arrays.toString(allowedExtensions));
                }

                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Users user = usersRepository.findByEmail(email)
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No user is logged in!"));
                try {
                    File file1 = convertMultiPartToFile(file);
                    s3.putObject(bucketName, originalFileName, file1);
                    file1.delete();
                    LearningMaterial learningMaterial = new LearningMaterial();
                    String[] parts = originalFileName.split("\\.(?=[^\\.]+$)"); // Splits at the last dot to separate name and extension
                    if (parts.length == 2) {
                        String filename = parts[0];
                        String filetype = parts[1];
                        // If needed, manipulate or validate filename and filetype here
                        learningMaterial.setFileName(filename);
                        learningMaterial.setFileType(filetype);
                        learningMaterial.setTrainingContent(trainingContent);
                        learningMaterial.setUploadBy(user.getName());
                        learningMaterial.setUploadDate();
                        trainingContent.getLearningMaterials().add(learningMaterial);
                        trainingContentRepository.save(trainingContent);
                        savedFileNames.add(filename + "." + filetype);
                    } else {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid file type inputted!");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new MaterialRes(savedFileNames, listOfMaterialDeleted);
    }


//    @Override
//    public byte[] downloadFile(String fileName, Long trainingContentId){
//        S3Object object = s3.getObject(bucketName, fileName);
//        S3ObjectInputStream objectContent = object.getObjectContent();
//        try {
//            return IOUtils.toByteArray(objectContent);
//        }catch (IOException e){
//            throw new RuntimeException(e);
//        }
//    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
            return convFile;
        }
    }
//    @Override
//    public String generateUrl(String fileName, org.springframework.http.HttpMethod httpMethod, Long trainingContentId) {
//        TrainingContent trainingContent = trainingContentRepository.findById(trainingContentId)
//                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Can not find Training Content!"));
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.HOUR,1);
//        URL url = s3.generatePresignedUrl(bucketName, fileName, calendar.getTime(), HttpMethod.valueOf(httpMethod.name()));
//        return url.toString();
//    }

    @Override
    public String generateUrl(String fileName, org.springframework.http.HttpMethod httpMethod, Long trainingContentId) {
        TrainingContent trainingContent = trainingContentRepository.findById(trainingContentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Can not find Training Content!"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR,1);
        URL url = s3.generatePresignedUrl(bucketName, fileName, calendar.getTime(), HttpMethod.valueOf(httpMethod.name()));

        // Check if url is null
        if (url == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Can not have url");
        }

        return url.toString();
    }
}
