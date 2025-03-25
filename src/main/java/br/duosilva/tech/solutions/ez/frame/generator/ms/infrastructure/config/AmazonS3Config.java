package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AmazonS3Config {
	
	private final AmazonProperties amazonProperties;

    public AmazonS3Config(AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
            .region(Region.of(amazonProperties.getRegion()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        amazonProperties.getCredentials().getAccessKey(),
                        amazonProperties.getCredentials().getSecretKey()
                    )
                )
            );

        String endpoint = amazonProperties.getS3().getEndpoint();

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                   .forcePathStyle(true); // importante para LocalStack
        }

        return builder.build();
    }
    
    @Bean
    public S3Presigner s3Presigner() {
        S3Presigner.Builder builder = S3Presigner.builder()
            .region(Region.of(amazonProperties.getRegion()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        amazonProperties.getCredentials().getAccessKey(),
                        amazonProperties.getCredentials().getSecretKey()
                    )
                )
            );

        
        String endpoint = amazonProperties.getS3().getEndpoint();
        if (endpoint != null && !endpoint.isBlank()) {
        	// IMPORTANTE: Evitar virtual-host-style para LocalStack — use 127.0.0.1
            builder.endpointOverride(URI.create(endpoint)); // <-- Isso garante path-style em localhost
        }

        return builder.build();
    }

}
