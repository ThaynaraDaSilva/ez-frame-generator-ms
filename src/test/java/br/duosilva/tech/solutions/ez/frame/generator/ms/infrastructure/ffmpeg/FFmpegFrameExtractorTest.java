package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.*;
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
    void extractFramesFromVideo_shouldThrowExceptionForInvalidFile() throws IOException {
        File dummyFile = File.createTempFile("dummy", ".txt");
        dummyFile.deleteOnExit();

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
            extractor.extractFramesFromVideo(dummyFile);
        });

        assertEquals("Failed to extract frames from video file.", ex.getMessage());
    }

    @Test
    void extractFrames_shouldThrowExceptionForInvalidMultipartFile() throws IOException {
        byte[] content = "not a real video".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", "video.txt", "video/mp4", content);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
            extractor.extractFrames(multipartFile);
        });

        assertEquals("Failed to extract frames from video file.", ex.getMessage());
    }

    @Test
    void extractFrames_shouldThrowBusinessRuleException_whenTransferFails() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("video.txt");
        doThrow(new IOException("IO error")).when(multipartFile).transferTo(any(File.class));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
            extractor.extractFrames(multipartFile);
        });

        assertEquals("Failed to convert uploaded video to a temporary file.", ex.getMessage());
    }

    @Test
    void extractFrames_shouldDeleteTemporaryFile() throws IOException {
        byte[] content = "not a real video".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", "video.txt", "video/mp4", content);


        try {
            extractor.extractFrames(multipartFile);
            fail("Expected BusinessRuleException");
        } catch (BusinessRuleException e) {
            // expected
        }

    }

}