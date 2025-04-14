package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.http.VideoIngestionHttpClient;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoIngestionRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VideoIngestionIntegrationUseCaseTest {

    private VideoIngestionHttpClient videoIngestionHttpClient;
    private VideoIngestionIntegrationUseCase useCase;

    @BeforeEach
    void setup() {
        videoIngestionHttpClient = mock(VideoIngestionHttpClient.class);
        useCase = new VideoIngestionIntegrationUseCase(videoIngestionHttpClient);
    }

    @Test
    void shouldSetCompletedStatusCorrectly() {
        // Arrange
        String videoId = "123";
        String status = "COMPLETED";
        String presignedUrl = "http://url";

        ArgumentCaptor<VideoIngestionRequestDto> dtoCaptor = ArgumentCaptor.forClass(VideoIngestionRequestDto.class);

        // Act
        useCase.updateVideoProcessingStatus(videoId, status, presignedUrl);

        // Assert
        verify(videoIngestionHttpClient).updateVideoProcessingStatus(eq(videoId), dtoCaptor.capture());

        VideoIngestionRequestDto dto = dtoCaptor.getValue();
        assertEquals("COMPLETED", dto.getStatus());
        assertEquals(presignedUrl, dto.getResultObjectKey());
        assertNull(dto.getErrorMessage());
    }

    @Test
    void shouldSetFailedStatusCorrectly() {
        // Arrange
        String videoId = "456";
        String status = "FAILED";
        String presignedUrl = "http://url";

        ArgumentCaptor<VideoIngestionRequestDto> dtoCaptor = ArgumentCaptor.forClass(VideoIngestionRequestDto.class);

        // Act
        useCase.updateVideoProcessingStatus(videoId, status, presignedUrl);

        // Assert
        verify(videoIngestionHttpClient).updateVideoProcessingStatus(eq(videoId), dtoCaptor.capture());

        VideoIngestionRequestDto dto = dtoCaptor.getValue();
        assertEquals("FAILED", dto.getStatus());
        assertNull(dto.getResultObjectKey());
        assertEquals("NÃO FOI POSSIVEL PROCESSAR O VIDEO", dto.getErrorMessage());
    }
}
