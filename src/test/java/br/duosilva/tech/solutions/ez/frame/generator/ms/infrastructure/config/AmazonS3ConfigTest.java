package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmazonS3ConfigTest {

    @Mock
    private AmazonProperties amazonProperties;

    @Mock
    private AmazonProperties.Credentials credentials;

    @Mock
    private AmazonProperties.S3 s3;

    @InjectMocks
    private AmazonS3Config amazonS3Config;

    @BeforeEach
    void setUp() {
        when(amazonProperties.getCredentials()).thenReturn(credentials);
        when(amazonProperties.getS3()).thenReturn(s3);
        when(amazonProperties.getRegion()).thenReturn("us-east-1");
        when(credentials.getAccessKey()).thenReturn("test-access-key");
        when(credentials.getSecretKey()).thenReturn("test-secret-key");
    }

    @Test
    void shouldCreateS3ClientWithoutEndpointOverride() {
        // Arrange
        when(s3.getEndpoint()).thenReturn(null);

        // Act
        S3Client client = amazonS3Config.s3Client();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(s3).getEndpoint();
    }

    @Test
    void shouldCreateS3ClientWithEndpointOverride() {
        // Arrange
        String endpoint = "http://localhost:4566";
        when(s3.getEndpoint()).thenReturn(endpoint);

        // Act
        S3Client client = amazonS3Config.s3Client();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(s3).getEndpoint();
    }

    @Test
    void shouldCreateS3ClientWithBlankEndpoint() {
        // Arrange
        when(s3.getEndpoint()).thenReturn("");

        // Act
        S3Client client = amazonS3Config.s3Client();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(s3).getEndpoint();
    }

    @Test
    void shouldCreateS3PresignerWithoutEndpointOverride() {
        // Arrange
        when(s3.getEndpoint()).thenReturn(null);

        // Act
        S3Presigner presigner = amazonS3Config.s3Presigner();

        // Assert
        assertNotNull(presigner);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(s3).getEndpoint();
    }

    @Test
    void shouldCreateS3PresignerWithEndpointOverride() {
        // Arrange
        String endpoint = "http://localhost:4566";
        when(s3.getEndpoint()).thenReturn(endpoint);

        // Act
        S3Presigner presigner = amazonS3Config.s3Presigner();

        // Assert
        assertNotNull(presigner);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(s3).getEndpoint();
    }

    @Test
    void shouldCreateS3PresignerWithBlankEndpoint() {
        // Arrange
        when(s3.getEndpoint()).thenReturn("");

        // Act
        S3Presigner presigner = amazonS3Config.s3Presigner();

        // Assert
        assertNotNull(presigner);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(s3).getEndpoint();
    }
}