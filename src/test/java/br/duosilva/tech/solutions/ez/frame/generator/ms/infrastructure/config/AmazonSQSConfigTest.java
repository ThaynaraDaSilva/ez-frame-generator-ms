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
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmazonSQSConfigTest {

    @Mock
    private AmazonProperties amazonProperties;

    @Mock
    private AmazonProperties.Credentials credentials;

    @Mock
    private AmazonProperties.Sqs sqs;

    @InjectMocks
    private AmazonSQSConfig amazonSQSConfig;

    @BeforeEach
    void setUp() {
        when(amazonProperties.getCredentials()).thenReturn(credentials);
        when(amazonProperties.getSqs()).thenReturn(sqs);
        when(amazonProperties.getRegion()).thenReturn("us-east-1");
        when(credentials.getAccessKey()).thenReturn("test-access-key");
        when(credentials.getSecretKey()).thenReturn("test-secret-key");
    }

    @Test
    void shouldCreateSqsClientWithoutEndpointOverride() {
        // Arrange
        when(sqs.getEndpoint()).thenReturn(null);

        // Act
        SqsClient client = amazonSQSConfig.sqsClient();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(sqs).getEndpoint();
    }

    @Test
    void shouldCreateSqsClientWithEndpointOverride() {
        // Arrange
        String endpoint = "http://localhost:4566";
        when(sqs.getEndpoint()).thenReturn(endpoint);

        // Act
        SqsClient client = amazonSQSConfig.sqsClient();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(sqs).getEndpoint();
    }

    @Test
    void shouldCreateSqsClientWithBlankEndpoint() {
        // Arrange
        when(sqs.getEndpoint()).thenReturn("");

        // Act
        SqsClient client = amazonSQSConfig.sqsClient();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(sqs).getEndpoint();
    }

    @Test
    void shouldCreateSqsAsyncClientWithoutEndpointOverride() {
        // Arrange
        when(sqs.getEndpoint()).thenReturn(null);

        // Act
        SqsAsyncClient asyncClient = amazonSQSConfig.sqsAsyncClient();

        // Assert
        assertNotNull(asyncClient);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(sqs).getEndpoint();
    }

    @Test
    void shouldCreateSqsAsyncClientWithEndpointOverride() {
        // Arrange
        String endpoint = "http://localhost:4566";
        when(sqs.getEndpoint()).thenReturn(endpoint);

        // Act
        SqsAsyncClient asyncClient = amazonSQSConfig.sqsAsyncClient();

        // Assert
        assertNotNull(asyncClient);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(sqs).getEndpoint();
    }

    @Test
    void shouldCreateSqsAsyncClientWithBlankEndpoint() {
        // Arrange
        when(sqs.getEndpoint()).thenReturn("");

        // Act
        SqsAsyncClient asyncClient = amazonSQSConfig.sqsAsyncClient();

        // Assert
        assertNotNull(asyncClient);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(sqs).getEndpoint();
    }
}