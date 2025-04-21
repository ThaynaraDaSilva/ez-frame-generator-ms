package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class AmazonDynamoDbConfig {
	
	private final AmazonProperties amazonProperties;

    public AmazonDynamoDbConfig(AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }
    
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(amazonProperties.getRegion()))
                .build(); // Nao define creds e nem endpoint
    }
    

}