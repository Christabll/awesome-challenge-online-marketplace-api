package com.awesomity.marketplace.marketplace_api.exceptionHandler;


import com.awesomity.marketplace.marketplace_api.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("/api/test");
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "id", 1L);

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleResourceNotFound(ex, webRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("User");
    }

    @Test
    void shouldHandleBadRequestException() {
        BadRequestException ex = new BadRequestException("Invalid input");

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleBadRequest(ex, webRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid input");
    }

    @Test
    void shouldHandleInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Wrong password");

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleInvalidCredentials(ex, webRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Wrong password");
    }

    @Test
    void shouldHandleUnAuthorizedException() {
        UnAuthorizedException ex = new UnAuthorizedException("Access denied");

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleUnAuthorized(ex, webRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("exceptions.unauthorized");

    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleGenericException(ex, webRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Something went wrong");
    }
}
