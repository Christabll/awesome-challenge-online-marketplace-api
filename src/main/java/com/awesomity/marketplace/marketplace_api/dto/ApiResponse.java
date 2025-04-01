package com.awesomity.marketplace.marketplace_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private T data;
    private String message = "";
    private HttpStatus status;
    private Object error = null;
    private final String timestamp = LocalDateTime.now().toString();

    public ApiResponse() {
    }

    public ApiResponse(String message, Object error, HttpStatus status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }

    public ApiResponse(T data, String message, Object error, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.error = error;
        this.status = status;
    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(T data, HttpStatus status) {
        this.data = data;
        this.status = status;
    }

    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public ApiResponse(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        assert this.status != null;
        return ResponseEntity.status(this.status).body(this);
    }

    // Static helper methods for creating error responses
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(null, message, null, HttpStatus.BAD_REQUEST);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(null, message, null, HttpStatus.NOT_FOUND);
    }

    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(null, message, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(data, message, null, HttpStatus.OK);
    }
    // Getters and setters ...
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getError() {
        return error;
    }
}
