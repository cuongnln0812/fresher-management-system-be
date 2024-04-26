package com.example.phase1_fams.dto.exception;

import com.example.phase1_fams.dto.response.ErrorRes;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ValidationException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
public class GlobalExceptionHandlerTests {
    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @Mock
    private BindingResult bindingResult;

    @Test
    public void handleApiException_ShouldReturnBadRequest() {
        ApiException exception = new ApiException(HttpStatus.NOT_FOUND, "Not Found");
        exception.setStatus(HttpStatus.NOT_FOUND);
        exception.setMessage("Not Found");
        when(webRequest.getDescription(false)).thenReturn("Some description");

        ResponseEntity<ErrorRes> responseEntity = globalExceptionHandler.handleApiException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Not Found", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        verify(webRequest).getDescription(false);
    }

    @Test
    public void handleResourceNotFoundException_ShouldReturnNotFoundLongFieldValue() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Training Program", "id", 123);
        when(webRequest.getDescription(false)).thenReturn("Resource not found");

        ResponseEntity<ErrorRes> responseEntity = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(exception.getResourceName(), "Training Program");
        assertEquals(exception.getFieldName(), "id");
        assertEquals(exception.getFieldValue(), 123);
        assertEquals("Training Program not found with id: '123'", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        verify(webRequest).getDescription(false);
    }

    @Test
    public void handleResourceNotFoundException_ShouldReturnNotFoundStringFieldValue() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Syllabus", "code", "NET123");
        when(webRequest.getDescription(false)).thenReturn("Resource not found");

        ResponseEntity<ErrorRes> responseEntity = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(exception.getResourceName(), "Syllabus");
        assertEquals(exception.getFieldName(), "code");
        assertEquals(exception.getStringFieldValue(), "NET123");
        assertEquals("Syllabus not found with code: 'NET123'", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        verify(webRequest).getDescription(false);
    }

    @Test
    public void handleValidationException_ShouldReturnBadRequest() {
        List<ErrorMessage> errorMessageList = new ArrayList<>();
        errorMessageList.add(new ErrorMessage("error 1"));
        errorMessageList.add(new ErrorMessage("error 2"));
        ValidationException exception = new ValidationException(errorMessageList);
        when(webRequest.getDescription(false)).thenReturn("Validation error");

        ResponseEntity<ErrorRes> responseEntity = globalExceptionHandler.handleValidationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(responseEntity.getBody()).getMessage());
        verify(webRequest).getDescription(false);
    }

    @Test
    void testHandleAuthenticationException() {
        // Arrange
        String expectedMessage = "Bad credentials";
        HttpStatus expectedStatus = HttpStatus.UNAUTHORIZED;
        AuthenticationException exception = new AuthenticationException(expectedMessage) {};

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleAuthenticationException(exception);

        // Assert
        assertEquals(expectedStatus, responseEntity.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
        assertNotNull(responseBody); // Ensure the body isn't null
        assertEquals(expectedMessage, responseBody.get("message"));
        assertEquals(expectedStatus.value(), responseBody.get("httpStatus"));
        assertEquals("failed", responseBody.get("status"));
        LocalDateTime timestamp = (LocalDateTime) responseBody.get("timestamp");
        assertNotNull(timestamp);
        assertTrue(LocalDateTime.now().minusSeconds(5).isBefore(timestamp));
    }

    @Test
    public void handleMethodArgumentNotValid_ShouldReturnValidationErrors() {
        HttpHeaders headers = new HttpHeaders();
        FieldError fieldError1 = new FieldError("objectName", "fieldName1", "Error message 1");
        FieldError fieldError2 = new FieldError("objectName", "fieldName2", "Error message 2");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        when(webRequest.getDescription(false)).thenReturn("Some description");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(exception, headers, HttpStatus.BAD_REQUEST, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, Objects.requireNonNull(responseEntity).getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
        assertEquals("failed", Objects.requireNonNull(responseBody).get("status"));
        assertEquals(List.of("Error message 1", "Error message 2"), responseBody.get("messages"));
        assertNotNull(responseBody.get("timestamp"));
        verify(bindingResult).getFieldErrors();
    }

    @Test
    public void handleAccessDeniedException_ShouldReturnUnauthorized() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        when(webRequest.getDescription(false)).thenReturn("Some description");

        ResponseEntity<ErrorRes> responseEntity = globalExceptionHandler.handleAccessDeniedException(exception, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("You do not have access to this!", Objects.requireNonNull(responseEntity.getBody()).getMessage());

        verify(webRequest).getDescription(false);
    }
}