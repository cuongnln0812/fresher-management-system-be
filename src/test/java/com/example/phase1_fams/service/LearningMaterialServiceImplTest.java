package com.example.phase1_fams.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.MaterialRes;
import com.example.phase1_fams.model.TrainingContent;
import com.example.phase1_fams.model.Users;
import com.example.phase1_fams.repository.LearningMaterialRepository;
import com.example.phase1_fams.repository.TrainingContentRepository;
import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.service.impl.LearningMaterialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



public class LearningMaterialServiceImplTest {

    @Mock
    private AmazonS3 s3;

    @Mock
    private LearningMaterialRepository learningMaterialRepository;

    @Mock
    private TrainingContentRepository trainingContentRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private LearningMaterialServiceImpl learningMaterialService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testSaveFile_Success() throws IOException {

        String email = "mockuser@gmail.com";
        Users user = new Users();
        TrainingContent trainingContent = new TrainingContent();
        List<Long> deletedList = new ArrayList<>();
        //Arrange
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(trainingContent));
        when(usersRepository.findByEmail("mockuser@gmail.com")).thenReturn(Optional.of(user));


        List<MockMultipartFile> mockMultipartFiles = new ArrayList<>();
        mockMultipartFiles.add(new MockMultipartFile("file1", "test1.xls", "text/plain", new byte[25 * 1024 * 1024]));
        mockMultipartFiles.add(new MockMultipartFile("file2", "test2.pdf", "text/plain", new byte[25 * 1024 * 1024]));

        List<MultipartFile> multipartFiles = mockMultipartFiles.stream()
                .map(mock -> (MultipartFile) mock)
                .collect(Collectors.toList());

        MaterialRes results =  learningMaterialService.saveFiles(multipartFiles, deletedList, 1L);

        assertNotNull(results);
        verify(trainingContentRepository, times(1)).findById(1L);
        verify(usersRepository, times(2)).findByEmail("mockuser@gmail.com");
    }





    @Test
    public void testSaveFile_NotFound() {
        // Mock user authentication
        String email = "mockuser@gmail.com";
        Users user = new Users();
        List<MultipartFile> multipartFiles = Arrays.asList(
                new MockMultipartFile("file1", "test1.txt", "text/plain", "Hello, World!".getBytes()),
                new MockMultipartFile("file2", "test2.txt", "text/plain", "Hello, World!".getBytes())
        );
        List<Long> deletedList = new ArrayList<>();

        // Stubbing chỉ cần thiết
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));
        lenient().when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(new TrainingContent()));

        // Test saveFile method
        assertThrows(ApiException.class, () -> learningMaterialService.saveFiles(multipartFiles, deletedList,1L),
                "Expected ApiException to be thrown when user is not found");
    }


    @Test
    public void testSaveFile_FileSizeExceedsLimit() throws IOException {
        // Arrange
        MockMultipartFile oversizedFile = new MockMultipartFile(
                "file",
                "test.xls",
                "text/plain",
                new byte[(25 * 1024 * 1024) + 1] // Exceeds the size limit
        );
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "valid.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        List<MultipartFile> multipartFiles = Arrays.asList(oversizedFile, validFile);
        List<Long> deletedList = new ArrayList<>();

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("mockuser@gmail.com");
        when(usersRepository.findByEmail("mockuser@gmail.com")).thenReturn(Optional.of(new Users()));
        when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(new TrainingContent()));

        // Test saveFiles method
        assertThrows(ApiException.class, () -> learningMaterialService.saveFiles(multipartFiles, deletedList,1L),
                "Expected ApiException to be thrown when file size exceeds the limit");

        // Verify


    }


    @Test
    public void testSaveFile_UnsupportedFileType() throws IOException {
        // Arrange
        MockMultipartFile unsupportedFile = new MockMultipartFile(
                "file",
                "test.txt", // Unsupported file type
                "text/plain",
                "Hello, World!".getBytes()
        );
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "valid.pdf",
                "application/pdf",
                new byte[1024] // Valid PDF file
        );
        List<MultipartFile> multipartFiles = Arrays.asList(unsupportedFile, validFile);
        List<Long> deletedList = new ArrayList<>();
        // Stubbing necessary calls
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("mockuser@gmail.com");
        when(usersRepository.findByEmail("mockuser@gmail.com")).thenReturn(Optional.of(new Users()));
        when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(new TrainingContent()));

        // Test saveFiles method
        assertThrows(ApiException.class, () -> learningMaterialService.saveFiles(multipartFiles, deletedList,1L),
                "Expected ApiException to be thrown when file type is unsupported");
    }
    @Test
    public void testSaveFile_NoUserLoggedIn() throws IOException {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(null);
        List<Long> deletedList = new ArrayList<>();
        List<MultipartFile> multipartFiles = Collections.singletonList(
                new MockMultipartFile(
                        "file",
                        "test.xls",
                        "text/plain",
                        "Hello, World!".getBytes()
                )
        );

        // Act & Assert
        assertThrows(ApiException.class, () -> {
            learningMaterialService.saveFiles(multipartFiles, deletedList,1L);
        });

    }


    @Test
    public void testSaveFile_NoTrainingContentFound() throws IOException {
        // Arrange
        String email = "mockuser@gmail.com";
        List<Long> deletedList = new ArrayList<>();
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(new Users())); // User exists

        List<MultipartFile> multipartFiles = Collections.singletonList(
                new MockMultipartFile(
                        "file",
                        "test.xls",
                        "text/plain",
                        "Hello, World!".getBytes()
                )
        );

        // Act & Assert
        assertThrows(ApiException.class, () -> {
            learningMaterialService.saveFiles(multipartFiles, deletedList,1L);
        });

        // Verify
        verify(trainingContentRepository).findById(1L);
    }



    @Test
    public void testSaveFile_InvalidFileTypeInputted() throws IOException {
        // Arrange
        String email = "mockuser@gmail.com";
        List<Long> deletedList = new ArrayList<>();

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(new Users())); // User exists
        when(trainingContentRepository.findById(1L)).thenReturn(Optional.of(new TrainingContent())); // Training content exists

        List<MultipartFile> multipartFiles = Collections.singletonList(
                new MockMultipartFile(
                        "file",
                        "test", // Invalid file type input
                        "text/plain",
                        "Hello, World!".getBytes()
                )
        );

        // Act & Assert
        assertThrows(ApiException.class, () -> {
            learningMaterialService.saveFiles(multipartFiles, deletedList,1L);
        });

        // Verify
        verify(s3, times(0)).putObject(anyString(), anyString(), (File) any());
    }

    @Test
    public void generateUrl_Successfully() {
        // Arrange
        Long trainingContentId = 1L;
        TrainingContent trainingContent = new TrainingContent();
        // assuming you have necessary setup for trainingContent
        when(trainingContentRepository.findById(anyLong())).thenReturn(Optional.of(trainingContent));
        // Mocking a null URL
        when(s3.generatePresignedUrl(anyString(), anyString(), any(), any())).thenReturn(null);

        // Act & Assert
        String bucketName = "example-bucket"; // Ensure bucketName has a valid format
        String fileName = "example.xls"; // Ensure fileName has a valid format
        try {
            String generatedUrl = learningMaterialService.generateUrl(bucketName, org.springframework.http.HttpMethod.GET, trainingContentId);
            // If no exception is thrown, fail the test
            fail("Expected ApiException but no exception was thrown");
        } catch (ApiException e) {
            // Assert that the exception message is as expected
            assertEquals("Can not have url", e.getMessage());
        }

        // Verify that the repository and s3 methods are called as expected
        verify(trainingContentRepository, times(1)).findById(anyLong());
        verify(s3, times(0)).generatePresignedUrl(eq(fileName), eq(bucketName), any(), any()); // Note the order of parameters is reversed
    }


    @Test
    public void generateUrl_NotFoundTrainingContent() {
        // Arrange
        Long trainingContentId = 1L;
        when(trainingContentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act and Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            learningMaterialService.generateUrl("fileName", org.springframework.http.HttpMethod.GET, trainingContentId);
        });

        // Verify that the exception is thrown with the expected status code and message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Can not find Training Content!", exception.getMessage());
    }

    @Test
    public void generateUrl_NotHaveUrl() {
        // Arrange
        Long trainingContentId = 1L;
        TrainingContent trainingContent = new TrainingContent();
        when(trainingContentRepository.findById(anyLong())).thenReturn(Optional.of(trainingContent));
        when(s3.generatePresignedUrl(anyString(), anyString(), any(), any())).thenReturn(null);

        // Act & Assert
        try {
            learningMaterialService.generateUrl("example.txt", org.springframework.http.HttpMethod.GET, trainingContentId);
        } catch (ApiException e) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
            assertEquals("Can not have url", e.getMessage());
        }

        // Verify that the repository and s3 methods are called as expected
        verify(trainingContentRepository, times(1)).findById(anyLong());
        verify(s3, times(0)).generatePresignedUrl(anyString(), anyString(), any(), any());
    }


}
