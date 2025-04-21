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
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmazonDynamoDbConfigTest {

    @Mock
    private AmazonProperties amazonProperties;

    @Mock
    private AmazonProperties.Credentials credentials;

    @Mock
    private AmazonProperties.DynamoDb dynamodb;

    @InjectMocks
    private AmazonDynamoDbConfig amazonDynamoDbConfig;

    @BeforeEach
    void setUp() {
        when(amazonProperties.getCredentials()).thenReturn(credentials);
        when(amazonProperties.getDynamodb()).thenReturn(dynamodb);
        when(amazonProperties.getRegion()).thenReturn("us-east-1");
        when(credentials.getAccessKey()).thenReturn("test-access-key");
        when(credentials.getSecretKey()).thenReturn("test-secret-key");
    }

    @Test
    void shouldCreateDynamoDbClientWithoutEndpointOverride() {
        // Arrange
        when(dynamodb.getEndpoint()).thenReturn(null);

        // Act
        DynamoDbClient client = amazonDynamoDbConfig.dynamoDbClient();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(dynamodb).getEndpoint();
    }

    @Test
    void shouldCreateDynamoDbClientWithEndpointOverride() {
        // Arrange
        String endpoint = "http://localhost:8000";
        when(dynamodb.getEndpoint()).thenReturn(endpoint);

        // Act
        DynamoDbClient client = amazonDynamoDbConfig.dynamoDbClient();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(dynamodb).getEndpoint();
    }

    @Test
    void shouldCreateDynamoDbClientWithBlankEndpoint() {
        // Arrange
        when(dynamodb.getEndpoint()).thenReturn("");

        // Act
        DynamoDbClient client = amazonDynamoDbConfig.dynamoDbClient();

        // Assert
        assertNotNull(client);
        verify(amazonProperties).getRegion();
        verify(amazonProperties, times(2)).getCredentials();
        verify(credentials).getAccessKey();
        verify(credentials).getSecretKey();
        verify(dynamodb).getEndpoint();
    }
}