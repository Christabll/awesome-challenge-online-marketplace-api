//package com.awesomity.marketplace.marketplace_api.exception;
//
//import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
//import org.hibernate.exception.ConstraintViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.Objects;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.fail(ex.getMessage()));
//    }
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.fail(ex.getMessage()));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
//        FieldError error = Objects.requireNonNull(ex.getFieldError());
//        String message = error.getField() + ": " + error.getDefaultMessage();
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.fail(message));
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
//        String message = ex.getMessage() + " - " + ex.getSQL() + " - " + ex.getSQLState();
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.fail(message));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex) {
//        // For unexpected exceptions, you may choose to return a generic message or log details
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.fail("Internal server error: " + ex.getMessage()));
//    }
//}
