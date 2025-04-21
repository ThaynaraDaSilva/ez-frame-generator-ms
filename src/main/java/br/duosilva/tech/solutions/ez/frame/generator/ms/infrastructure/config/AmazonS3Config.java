package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AmazonS3Config {
	
	private final AmazonProperties amazonProperties;

    public AmazonS3Config(AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }

    
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(amazonProperties.getRegion()))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build(); // Sem credentialsProvider nem endpointOverride
    }
    
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(amazonProperties.getRegion()))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build(); // Sem credentialsProvider nem endpointOverride
    }

}
