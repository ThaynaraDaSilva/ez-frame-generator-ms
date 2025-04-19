package br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    private ErrorResponse errorResponse;

    @BeforeEach
    void setUp() {
        errorResponse = new ErrorResponse("Test error message");
    }

    @Test
    void shouldCreateErrorResponseWithMessageViaConstructor() {
        // Arrange
        String message = "Constructor test message";

        // Act
        ErrorResponse response = new ErrorResponse(message);

        // Assert
        assertEquals(message, response.getMessage());
    }

    @Test
    void shouldSetAndGetMessage() {
        // Arrange
        String newMessage = "Updated error message";

        // Act
        errorResponse.setMessage(newMessage);

        // Assert
        assertEquals(newMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleNullMessageViaConstructor() {
        // Act
        ErrorResponse response = new ErrorResponse(null);

        // Assert
        assertNull(response.getMessage());
    }

    @Test
    void shouldHandleNullMessageViaSetter() {
        // Act
        errorResponse.setMessage(null);

        // Assert
        assertNull(errorResponse.getMessage());
    }
}