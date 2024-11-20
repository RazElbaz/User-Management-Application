package com.example.usermanagement.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


@ControllerAdvice // Applies exception handling globally to all controllers
public class GlobalExceptionHandler {

    // Handles validation errors for invalid method arguments (e.g., invalid email format)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMessages = new HashMap<>();

        // Collect error messages, including the specific field name and message
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        });


        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);   // Return 400 for validation errors
    }

    // Handles ConstraintViolationException for validation annotations (e.g., custom constraints)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errorMessages = new HashMap<>();

        // Collect constraint violation errors (including email format)
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            // Add each violation's property and message to the map
            errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST); // Return 400 for violations
    }

    // Handles missing request parameter errors
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        String errorMessage = "Missing required parameter: " + ex.getParameterName();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);  // Return 400 for missing params
    }

    // Handles unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Return 500
    }
    // Handles HTTP method not supported exceptions
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>("Invalid HTTP method: " + ex.getMethod(), HttpStatus.BAD_REQUEST); // Return 400
    }
    // Handle NoSuchElementException (e.g., user not found)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // Return 404
    }

    // Handle IllegalArgumentException (e.g., invalid status, password mismatch)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());// Return 400
    }
    // Handles access denied errors (e.g., insufficient permissions)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>("Access denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);// Return 403
    }
}
