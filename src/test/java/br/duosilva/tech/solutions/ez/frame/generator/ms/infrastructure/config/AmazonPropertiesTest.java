package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmazonPropertiesTest {

    private AmazonProperties amazonProperties;

    @BeforeEach
    void setUp() {
        amazonProperties = new AmazonProperties();
    }

    @Test
    void shouldSetAndGetRegion() {
        // Arrange
        String region = "us-east-1";

        // Act
        amazonProperties.setRegion(region);

        // Assert
        assertEquals(region, amazonProperties.getRegion());
    }

    @Test
    void shouldSetAndGetCredentials() {
        // Arrange
        AmazonProperties.Credentials credentials = new AmazonProperties.Credentials();
        credentials.setAccessKey("test-access-key");
        credentials.setSecretKey("test-secret-key");

        // Act
        amazonProperties.setCredentials(credentials);

        // Assert
        assertEquals(credentials, amazonProperties.getCredentials());
        assertEquals("test-access-key", amazonProperties.getCredentials().getAccessKey());
        assertEquals("test-secret-key", amazonProperties.getCredentials().getSecretKey());
    }

    @Test
    void shouldSetAndGetS3() {
        // Arrange
        AmazonProperties.S3 s3 = new AmazonProperties.S3();
        s3.setBucketName("test-bucket");
        s3.setEndpoint("http://s3.localhost");

        // Act
        amazonProperties.setS3(s3);

        // Assert
        assertEquals(s3, amazonProperties.getS3());
        assertEquals("test-bucket", amazonProperties.getS3().getBucketName());
        assertEquals("http://s3.localhost", amazonProperties.getS3().getEndpoint());
    }

    @Test
    void shouldSetAndGetSqs() {
        // Arrange
        AmazonProperties.Sqs sqs = new AmazonProperties.Sqs();
        sqs.setQueueName("test-queue");
        sqs.setResultQueueName("test-result-queue");
        sqs.setEndpoint("http://sqs.localhost");

        // Act
        amazonProperties.setSqs(sqs);

        // Assert
        assertEquals(sqs, amazonProperties.getSqs());
        assertEquals("test-queue", amazonProperties.getSqs().getQueueName());
        assertEquals("test-result-queue", amazonProperties.getSqs().getResultQueueName());
        assertEquals("http://sqs.localhost", amazonProperties.getSqs().getEndpoint());
    }

    @Test
    void shouldSetAndGetDynamoDb() {
        // Arrange
        AmazonProperties.DynamoDb dynamoDb = new AmazonProperties.DynamoDb();
        dynamoDb.setTableName("test-table");
        dynamoDb.setEndpoint("http://dynamodb.localhost");

        // Act
        amazonProperties.setDynamodb(dynamoDb);

        // Assert
        assertEquals(dynamoDb, amazonProperties.getDynamodb());
        assertEquals("test-table", amazonProperties.getDynamodb().getTableName());
        assertEquals("http://dynamodb.localhost", amazonProperties.getDynamodb().getEndpoint());
    }

    @Test
    void shouldHandleNullValues() {
        // Act & Assert
        assertNull(amazonProperties.getRegion());
        assertNull(amazonProperties.getCredentials());
        assertNull(amazonProperties.getS3());
        assertNull(amazonProperties.getSqs());
        assertNull(amazonProperties.getDynamodb());
    }
}