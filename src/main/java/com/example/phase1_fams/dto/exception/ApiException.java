package com.example.phase1_fams.dto.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ApiException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}