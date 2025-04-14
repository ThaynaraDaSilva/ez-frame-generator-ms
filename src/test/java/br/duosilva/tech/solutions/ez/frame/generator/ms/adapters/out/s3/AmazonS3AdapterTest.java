package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class AmazonS3AdapterTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private AmazonProperties amazonProperties;

    @Mock
    private AmazonProperties.S3 s3Config;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private AmazonS3Adapter amazonS3Adapter;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String OBJECT_KEY = "userId/test.zip";
    private static final Duration EXPIRATION = Duration.ofMinutes(10);

    @Test
    void uploadZipToS3_ShouldUploadFileSuccessfully(@TempDir File tempDir) throws IOException {
        // Arrange
        File zipFile = new File(tempDir, "test.zip");
        zipFile.createNewFile();
        when(amazonProperties.getS3()).thenReturn(s3Config);
        when(s3Config.getBucketName()).thenReturn(BUCKET_NAME);

        // Act
        amazonS3Adapter.uploadZipToS3(OBJECT_KEY, zipFile);

        // Assert
        verify(s3Client).putObject(
                argThat((PutObjectRequest request) ->
                        request.bucket().equals(BUCKET_NAME) &&
                                request.key().equals(OBJECT_KEY) &&
                                request.contentType().equals("application/zip")),
                argThat((RequestBody body) -> body != null)
        );
    }

    @Test
    void generatePresignedUrl_ShouldReturnValidUrl() throws Exception {
        // Arrange
        when(amazonProperties.getS3()).thenReturn(s3Config);
        when(s3Config.getBucketName()).thenReturn(BUCKET_NAME);
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        URL mockUrl = new URL("https://test-bucket.s3.amazonaws.com/" + OBJECT_KEY);
        when(presignedRequest.url()).thenReturn(mockUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        // Act
        String result = amazonS3Adapter.generatePresignedUrl(OBJECT_KEY, EXPIRATION);

        // Assert
        assertEquals(mockUrl.toString(), result);
        verify(s3Presigner).presignGetObject(argThat((GetObjectPresignRequest request) ->
                request.getObjectRequest().bucket().equals(BUCKET_NAME) &&
                        request.getObjectRequest().key().equals(OBJECT_KEY) &&
                        request.signatureDuration().equals(EXPIRATION)
        ));
    }

    @Test
    void downloadVideo_ShouldReturnInputStream_WhenObjectExists() throws IOException {
        // Arrange
        ResponseInputStream<GetObjectResponse> responseInputStream = mock(ResponseInputStream.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

        // Act
        InputStream result = amazonS3Adapter.downloadVideo(BUCKET_NAME, OBJECT_KEY);

        // Assert
        assertNotNull(result);
        verify(s3Client).getObject(argThat((GetObjectRequest request) ->
                request.bucket().equals(BUCKET_NAME) &&
                        request.key().equals(OBJECT_KEY)
        ));
    }

    @Test
    void downloadVideo_ShouldThrowBusinessRuleException_WhenS3ThrowsException() {
        // Arrange
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(new RuntimeException("S3 error"));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                amazonS3Adapter.downloadVideo(BUCKET_NAME, OBJECT_KEY)
        );
        assertEquals("FAILED TO DOWNLOAD S3 OBJECT: " + BUCKET_NAME + "/" + OBJECT_KEY, exception.getMessage());
    }
}