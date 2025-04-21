package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3.AmazonS3Adapter;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoDataResponseDto;
import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service.VideoProcessingService;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils.FileUtils;

class FrameGeneratorUseCaseTest {

	@Mock
    private AmazonS3Adapter amazonS3Adapter;

    @Mock
    private VideoProcessingService videoProcessingService;

    @Mock
    private VideoIngestionIntegrationUseCase ingestionUseCase;

    @InjectMocks
    private FrameGeneratorUseCase useCase;

    @TempDir
    Path tempDir;

    private File tempVideoFile;
    private File tempZipFile;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        tempVideoFile = File.createTempFile("video", ".mp4", tempDir.toFile());
        tempZipFile = File.createTempFile("frames", ".zip", tempDir.toFile());
    }

    @Test
    void shouldProcessVideoSuccessfully() throws Exception {
        // given
        VideoDataResponseDto dto = new VideoDataResponseDto(
                "video_1", "video.mp4", "bucket", "key", "12:00", "user_1", "test@test.com"
        );

        InputStream inputStream = new ByteArrayInputStream("dummy".getBytes());

        // mock static FileUtils
        try (MockedStatic<FileUtils> fileUtilsMockedStatic = mockStatic(FileUtils.class)) {

            when(amazonS3Adapter.downloadVideo("bucket", "key")).thenReturn(inputStream);
            fileUtilsMockedStatic.when(() -> FileUtils.convertStreamToFile(any(), eq(".mp4")))
                    .thenReturn(tempVideoFile);

            when(videoProcessingService.generateVideoFrames(tempVideoFile)).thenReturn(tempZipFile);
            when(amazonS3Adapter.doesZipExistInS3(any())).thenReturn(false);
            when(amazonS3Adapter.generatePresignedUrl(any(), any())).thenReturn("http://presigned-url");

            // when
            useCase.initiateFrameGenerationProcess(dto);

            // then
            verify(amazonS3Adapter).uploadZipToS3(any(), eq(tempZipFile));
            verify(amazonS3Adapter).generatePresignedUrl(any(), any());
            verify(ingestionUseCase).updateVideoProcessingStatus("video_1", "COMPLETED", "http://presigned-url");
        }
    }

    @Test
    void shouldThrowBusinessRuleExceptionOnFailure() throws Exception {
        // given
        VideoDataResponseDto dto = new VideoDataResponseDto(
                "video_1", "video.mp4", "bucket", "key", "12:00", "user_1", "test@test.com"
        );

        when(amazonS3Adapter.downloadVideo("bucket", "key"))
                .thenThrow(new RuntimeException("Download error"));

        // when & then
        assertThrows(BusinessRuleException.class, () -> useCase.initiateFrameGenerationProcess(dto));
    }
}
