package com.project.exception;




import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.project.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(DatasetNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDatasetNotFound(
            DatasetNotFoundException ex,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }
	
	@ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidQuery(
            InvalidQueryException ex,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid or missing JSON request body. Please provide a valid JSON object.",
            request.getRequestURI()
        );
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid parameter value for '" + ex.getName() + "'",
            request.getRequestURI()
        );
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedError(
            Exception ex,
            HttpServletRequest request) {
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred while processing the request.",
            request.getRequestURI()
        );
    }
	
	private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path) {
        ApiErrorResponse error = new ApiErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path
        );
        return ResponseEntity.status(status).body(error);
    }
}
