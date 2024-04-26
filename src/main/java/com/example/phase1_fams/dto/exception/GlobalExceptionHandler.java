package com.example.phase1_fams.dto.exception;

import com.example.phase1_fams.dto.response.ErrorRes;
import org.modelmapper.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.access.AccessDeniedException;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorRes> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request
    ) {
        ErrorRes errorDetails = new ErrorRes();
        errorDetails.setMessage(ex.getMessage());
        errorDetails.setHttpStatus(HttpStatus.NOT_FOUND.value());
        errorDetails.setDetails(request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorRes> handleApiException(
            ApiException ex,
            WebRequest request
    ) {
        ErrorRes errorDetails = new ErrorRes();
        errorDetails.setMessage(ex.getMessage());
        errorDetails.setHttpStatus(ex.getStatus().value());
        errorDetails.setDetails(request.getDescription(false));
        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorRes> handleValidationException(
            ValidationException ex,
            WebRequest request
    ) {
        ErrorRes errorDetails = new ErrorRes();
        errorDetails.setMessage(ex.getMessage());
        errorDetails.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setDetails(request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", "failed");
        body.put("httpStatus", HttpStatus.UNAUTHORIZED.value());
        body.put("message", "Bad credentials");
        // Do not include the stack trace in the response
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRes> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request
    ) {
        ErrorRes errorDetails = new ErrorRes();
        errorDetails.setMessage("You do not have access to this!");
        errorDetails.setHttpStatus(HttpStatus.UNAUTHORIZED.value());
        errorDetails.setDetails(request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", "failed");
        body.put("httpStatus", status.value());

        //get all the errors
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        body.put("messages", errors);

        return new ResponseEntity<>(body, status);
    }
}
