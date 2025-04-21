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

	    @InjectMocks
	    private AmazonSQSConfig amazonSQSConfig;

	    @BeforeEach
	    void setUp() {
	        when(amazonProperties.getRegion()).thenReturn("us-east-1");
	    }

	    @Test
	    void shouldCreateSqsClient() {
	        // Act
	        SqsClient client = amazonSQSConfig.sqsClient();

	        // Assert
	        assertNotNull(client);
	        verify(amazonProperties).getRegion();
	    }

	    @Test
	    void shouldCreateSqsAsyncClient() {
	        // Act
	        SqsAsyncClient asyncClient = amazonSQSConfig.sqsAsyncClient();

	        // Assert
	        assertNotNull(asyncClient);
	        verify(amazonProperties).getRegion();
	    }
}