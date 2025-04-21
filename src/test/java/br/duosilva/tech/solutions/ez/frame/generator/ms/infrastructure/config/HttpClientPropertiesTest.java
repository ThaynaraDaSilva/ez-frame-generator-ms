package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientPropertiesTest {

    private HttpClientProperties httpClientProperties;

    @BeforeEach
    void setUp() {
        httpClientProperties = new HttpClientProperties();
    }

    @Test
    void shouldSetAndGetVideoIngestionEndpoint() {
        // Arrange
        String endpoint = "http://localhost:8080/video-ingestion";

        // Act
        httpClientProperties.setVideoIngestionEndpoint(endpoint);

        // Assert
        assertEquals(endpoint, httpClientProperties.getVideoIngestionEndpoint());
    }

    @Test
    void shouldHandleNullVideoIngestionEndpoint() {
        // Act
        httpClientProperties.setVideoIngestionEndpoint(null);

        // Assert
        assertNull(httpClientProperties.getVideoIngestionEndpoint());
    }

    @Test
    void shouldReturnNullWhenVideoIngestionEndpointNotSet() {
        // Assert
        assertNull(httpClientProperties.getVideoIngestionEndpoint());
    }
}