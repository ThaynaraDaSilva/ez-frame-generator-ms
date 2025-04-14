package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoMetadataTest {

    private VideoMetadata videoMetadata;

    @BeforeEach
    void setUp() {
        videoMetadata = new VideoMetadata(
                UUID.randomUUID().toString(),
                "example.mp4",
                "video/mp4",
                1024,
                LocalDateTime.now(),
                "user-123",
                "user@example.com",
                ProcessingStatus.PENDING,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void testConstructor() {
        assertNotNull(videoMetadata);
        assertNotNull(videoMetadata.getId());
        assertEquals("example.mp4", videoMetadata.getOriginalFileName());
        assertEquals("video/mp4", videoMetadata.getContentType());
        assertEquals(1024, videoMetadata.getFileSizeBytes());
        assertEquals("user-123", videoMetadata.getUserId());
        assertEquals("user@example.com", videoMetadata.getUserEmail());
        assertEquals(ProcessingStatus.PENDING, videoMetadata.getStatus());
    }

    @Test
    void testMarkAsProcessing() {
        videoMetadata.markAsProcessing();
        assertEquals(ProcessingStatus.PROCESSING, videoMetadata.getStatus());
    }

    @Test
    void testMarkAsCompleted() {
        videoMetadata.markAsCompleted("bucket-name", "object-key");
        assertEquals(ProcessingStatus.COMPLETED, videoMetadata.getStatus());
        assertEquals("bucket-name", videoMetadata.getResultBucketName());
        assertEquals("object-key", videoMetadata.getResultObjectKey());
        assertNotNull(videoMetadata.getProcessedAt());
    }

    @Test
    void testMarkAsFailed() {
        videoMetadata.markAsFailed("An error occurred");
        assertEquals(ProcessingStatus.FAILED, videoMetadata.getStatus());
        assertEquals("An error occurred", videoMetadata.getErrorMessage());
        assertNotNull(videoMetadata.getProcessedAt());
    }

    @Test
    void testGetters() {
        assertEquals("example.mp4", videoMetadata.getOriginalFileName());
        assertEquals("video/mp4", videoMetadata.getContentType());
        assertEquals(1024, videoMetadata.getFileSizeBytes());
        assertEquals("user-123", videoMetadata.getUserId());
        assertEquals("user@example.com", videoMetadata.getUserEmail());
    }
}