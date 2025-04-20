
package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg.FFmpegFrameExtractor;

class VideoProcessingServiceTest {

	@Mock
	private FFmpegFrameExtractor ffmpegFrameExtractor;

	@InjectMocks
	private VideoProcessingService videoProcessingService;

	private File expectedZipFile;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		expectedZipFile = new File("video-frames.zip");
	}

	@Test
	void testGenerateVideoFrames_ShouldReturnZipFile() {
		// Arrange
		File inputVideoFile = new File("input.mp4");

		when(ffmpegFrameExtractor.extractFramesFromVideo(inputVideoFile)).thenReturn(expectedZipFile);

		// Act
		File result = videoProcessingService.generateVideoFrames(inputVideoFile);

		// Assert
		assertEquals(expectedZipFile, result);
		verify(ffmpegFrameExtractor).extractFramesFromVideo(inputVideoFile);
	}

	@Test
	void testGenerateFrames_ShouldReturnZipFile() {
		// Arrange
		MultipartFile multipartFile = mock(MultipartFile.class);

		when(ffmpegFrameExtractor.extractFrames(multipartFile)).thenReturn(expectedZipFile);

		// Act
		File result = videoProcessingService.generateFrames(multipartFile);

		// Assert
		assertEquals(expectedZipFile, result);
		verify(ffmpegFrameExtractor).extractFrames(multipartFile);
	}
}
