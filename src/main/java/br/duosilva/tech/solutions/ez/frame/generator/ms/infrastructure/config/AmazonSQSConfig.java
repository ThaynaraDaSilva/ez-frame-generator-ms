package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;

@Configuration
public class AmazonSQSConfig {

	private final AmazonProperties amazonProperties;

	public AmazonSQSConfig(AmazonProperties amazonProperties) {
		this.amazonProperties = amazonProperties;

	}

	@Bean
	public SqsClient sqsClient() {
		SqsClientBuilder builder = SqsClient.builder().region(Region.of(amazonProperties.getRegion()))
				.credentialsProvider(StaticCredentialsProvider
						.create(AwsBasicCredentials.create(amazonProperties.getCredentials().getAccessKey(),
								amazonProperties.getCredentials().getSecretKey())));

		String endpoint = amazonProperties.getSqs().getEndpoint();

		if (endpoint != null && !endpoint.isBlank()) {
			builder.endpointOverride(URI.create(endpoint));
		}

		return builder.build();
	}

	@Bean
	public SqsAsyncClient sqsAsyncClient() {
		SqsAsyncClientBuilder builder = SqsAsyncClient.builder().region(Region.of(amazonProperties.getRegion()))
				.credentialsProvider(StaticCredentialsProvider
						.create(AwsBasicCredentials.create(amazonProperties.getCredentials().getAccessKey(),
								amazonProperties.getCredentials().getSecretKey())));

		String endpoint = amazonProperties.getSqs().getEndpoint();

		if (endpoint != null && !endpoint.isBlank()) {
			builder.endpointOverride(URI.create(endpoint));
		}

		return builder.build();
	}

}