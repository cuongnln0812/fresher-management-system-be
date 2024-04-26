package com.example.phase1_fams.utils;

import com.example.phase1_fams.dto.ApiResponse;
import com.example.phase1_fams.dto.exception.ApiException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@UtilityClass
public class ResponseUtils {
    public static ResponseEntity<ApiResponse> response(Object data, String message, HttpStatus status){
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .data(data)
                        .status("success")
                        .message(message)
                        .timestamp(Instant.now())
                        .httpStatus(status.value())
                        .build()
                , status
        );
    }

    public static ResponseEntity<ApiResponse> ok(String message, HttpStatus status){
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .data("null")
                        .status("success")
                        .message(message)
                        .timestamp(Instant.now())
                        .httpStatus(status.value())
                        .build()
                , status
        );
    }

    public static ResponseEntity<ApiResponse> error(
            ApiException e){
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .data("null")
                        .status("failed")
                        .message(e.getMessage())
                        .timestamp(Instant.now())
                        .httpStatus(e.getStatus().value())
                        .build()
                , e.getStatus()
        );
    }
}
