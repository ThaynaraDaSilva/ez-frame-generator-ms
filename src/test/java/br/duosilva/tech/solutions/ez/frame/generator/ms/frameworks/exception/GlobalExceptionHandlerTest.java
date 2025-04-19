package br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleBusinessRuleException() {
        // Arrange
        String errorMessage = "Business rule violated";
        BusinessRuleException exception = new BusinessRuleException(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessRuleException(exception);

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        // Arrange
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void shouldHandleGenericException() {
        // Arrange
        String errorMessage = "Unexpected error occurred";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: " + errorMessage, response.getBody());
    }
}