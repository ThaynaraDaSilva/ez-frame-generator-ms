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

	    @InjectMocks
	    private AmazonS3Config amazonS3Config;

	    @BeforeEach
	    void setUp() {
	        when(amazonProperties.getRegion()).thenReturn("us-east-1");
	    }

	    @Test
	    void shouldCreateS3Client() {
	        // Act
	        S3Client client = amazonS3Config.s3Client();

	        // Assert
	        assertNotNull(client);
	        verify(amazonProperties).getRegion();
	    }

	    @Test
	    void shouldCreateS3Presigner() {
	        // Act
	        S3Presigner presigner = amazonS3Config.s3Presigner();

	        // Assert
	        assertNotNull(presigner);
	        verify(amazonProperties).getRegion();
	    }
}