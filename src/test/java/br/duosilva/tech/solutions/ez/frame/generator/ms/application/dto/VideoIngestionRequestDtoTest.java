package br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoIngestionRequestDtoTest {

    @Test
    void testDefaultConstructor() {
        // Arrange
        VideoIngestionRequestDto dto = new VideoIngestionRequestDto();

        // Act & Assert
        assertNull(dto.getStatus());
        assertNull(dto.getResultObjectKey());
        assertNull(dto.getErrorMessage());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        // Arrange
        String status = "PROCESSING";
        String resultObjectKey = "result/object/key";
        String errorMessage = "No errors";

        // Act
        VideoIngestionRequestDto dto = new VideoIngestionRequestDto(status, resultObjectKey, errorMessage);

        // Assert
        assertEquals(status, dto.getStatus());
        assertEquals(resultObjectKey, dto.getResultObjectKey());
        assertEquals(errorMessage, dto.getErrorMessage());
    }

    @Test
    void testSetters() {
        // Arrange
        VideoIngestionRequestDto dto = new VideoIngestionRequestDto();

        // Act
        dto.setStatus("COMPLETED");
        dto.setResultObjectKey("result/object/key");
        dto.setErrorMessage("No errors");

        // Assert
        assertEquals("COMPLETED", dto.getStatus());
        assertEquals("result/object/key", dto.getResultObjectKey());
        assertEquals("No errors", dto.getErrorMessage());
    }
}