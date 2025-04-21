package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AmazonSQSConfig {

	private final AmazonProperties amazonProperties;

	public AmazonSQSConfig(AmazonProperties amazonProperties) {
		this.amazonProperties = amazonProperties;

	}
	
	 @Bean
	    public SqsClient sqsClient() {
	        return SqsClient.builder()
	                .region(Region.of(amazonProperties.getRegion()))
	                .credentialsProvider(DefaultCredentialsProvider.create())
	                .build(); //Sem credentialsProvider e endpointOverride
	    }
	 
	 
	 @Bean
	 public SqsAsyncClient sqsAsyncClient() {
	     return SqsAsyncClient.builder()
	             .region(Region.of(amazonProperties.getRegion()))
	             .credentialsProvider(DefaultCredentialsProvider.create())
	             .build(); // Sem credentialsProvider e endpointOverride (ok se for LocalStack com configurações globais)
	 }


}