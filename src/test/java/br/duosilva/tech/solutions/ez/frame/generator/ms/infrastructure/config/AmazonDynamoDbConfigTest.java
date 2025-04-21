package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ExtendWith(MockitoExtension.class)
class AmazonDynamoDbConfigTest {
	@Mock
	private AmazonProperties amazonProperties;

	@InjectMocks
	private AmazonDynamoDbConfig amazonDynamoDbConfig;

	@BeforeEach
	void setUp() {
		when(amazonProperties.getRegion()).thenReturn("us-east-1");
	}

	@Test
	void shouldCreateDynamoDbClient() {
		// Act
		DynamoDbClient client = amazonDynamoDbConfig.dynamoDbClient();

		// Assert
		assertNotNull(client);
		verify(amazonProperties).getRegion();
	}

}