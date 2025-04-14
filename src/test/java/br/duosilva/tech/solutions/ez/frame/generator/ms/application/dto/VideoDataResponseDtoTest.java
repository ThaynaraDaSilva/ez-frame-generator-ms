package br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoDataResponseDtoTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String videoId = "12345";
        String originalFileName = "example.mp4";
        String s3BucketName = "my-bucket";
        String s3Key = "videos/example.mp4";
        String uploadTimestamp = "2023-10-01T12:00:00Z";
        String userId = "user-123";
        String userEmail = "user@example.com";

        // Act
        VideoDataResponseDto dto = new VideoDataResponseDto(videoId, originalFileName, s3BucketName, s3Key,
                uploadTimestamp, userId, userEmail);

        // Assert
        assertEquals(videoId, dto.getVideoId());
        assertEquals(originalFileName, dto.getOriginalFileName());
        assertEquals(s3BucketName, dto.getS3BucketName());
        assertEquals(s3Key, dto.getS3Key());
        assertEquals(uploadTimestamp, dto.getUploadTimestamp());
        assertEquals(userId, dto.getUserId());
        assertEquals(userEmail, dto.getUserEmai());
    }

    @Test
    void testSetters() {
        // Arrange
        VideoDataResponseDto dto = new VideoDataResponseDto("", "", "", "", "", "", "");

        // Act
        dto.setVideoId("12345");
        dto.setOriginalFileName("example.mp4");
        dto.setS3BucketName("my-bucket");
        dto.setS3Key("videos/example.mp4");
        dto.setUploadTimestamp("2023-10-01T12:00:00Z");
        dto.setUserId("user-123");
        dto.setUserEmai("user@example.com");

        // Assert
        assertEquals("12345", dto.getVideoId());
        assertEquals("example.mp4", dto.getOriginalFileName());
        assertEquals("my-bucket", dto.getS3BucketName());
        assertEquals("videos/example.mp4", dto.getS3Key());
        assertEquals("2023-10-01T12:00:00Z", dto.getUploadTimestamp());
        assertEquals("user-123", dto.getUserId());
        assertEquals("user@example.com", dto.getUserEmai());
    }

    @Test
    void testToString() {
        // Arrange
        VideoDataResponseDto dto = new VideoDataResponseDto("12345", "example.mp4", "my-bucket", "videos/example.mp4",
                "2023-10-01T12:00:00Z", "user-123", "user@example.com");

        // Act
        String result = dto.toString();

        // Assert
        String expected = "VideoDataResponseDTO [videoId=12345, originalFileName=example.mp4, s3BucketName=my-bucket, s3Key=videos/example.mp4, uploadTimestamp=2023-10-01T12:00:00Z, userId=user-123, userEmai=user@example.com]";
        assertEquals(expected, result);
    }
}