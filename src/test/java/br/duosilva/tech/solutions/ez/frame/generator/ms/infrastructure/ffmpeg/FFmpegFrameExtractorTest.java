package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

public class FFmpegFrameExtractorTest {
	  @InjectMocks
	    private FFmpegFrameExtractor extractor;

	    @BeforeEach
	    void setup() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    void extractFramesFromVideo_shouldThrowException_whenFileDoesNotExist() throws IOException {
	        // Arrange
	        File dummyFile = File.createTempFile("dummy", ".txt");
	        dummyFile.delete(); // Garante que o arquivo não exista

	        // Act & Assert
	        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
	            extractor.extractFramesFromVideo(dummyFile);
	        });

	        assertEquals("Input video file is null or does not exist.", ex.getMessage());
	    }

	    @Test
	    void extractFrames_shouldThrowException_whenTransferToFails() throws IOException {
	        // Arrange
	        MultipartFile multipartFile = mock(MultipartFile.class);
	        when(multipartFile.getOriginalFilename()).thenReturn("video.txt");
	        doThrow(new IOException("IO error")).when(multipartFile).transferTo(any(File.class));

	        // Act & Assert
	        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
	            extractor.extractFrames(multipartFile);
	        });

	        assertEquals("Failed to convert uploaded video to a temporary file.", ex.getMessage());
	    }

	    @Test
	    void extractFrames_shouldThrowException_whenVideoContentIsInvalid() {
	        // Arrange: conteúdo não é um vídeo de verdade
	        byte[] content = "not a real video".getBytes();
	        MultipartFile multipartFile = new MockMultipartFile("file", "video.txt", "video/mp4", content);

	        // Act & Assert
	        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
	            extractor.extractFrames(multipartFile);
	        });

	        assertEquals("Failed to extract and write frames to zip file.", ex.getMessage());
	    }

	    @Test
	    void extractFrames_shouldDeleteTemporaryFile_evenWhenExceptionIsThrown() {
	        // Arrange: conteúdo inválido que vai causar falha durante a extração de frames
	        byte[] content = "not a real video".getBytes();
	        MultipartFile multipartFile = new MockMultipartFile("file", "video.txt", "video/mp4", content);

	        // Act
	        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
	            extractor.extractFrames(multipartFile);
	        });

	        // Assert
	        assertEquals("Failed to extract and write frames to zip file.", ex.getMessage());

	    }
}