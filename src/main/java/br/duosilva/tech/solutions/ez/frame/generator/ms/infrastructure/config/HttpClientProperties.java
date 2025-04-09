package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "microservice")
public class HttpClientProperties {

	private String videoIngestionEndpoint;

	public String getVideoIngestionEndpoint() {
		return videoIngestionEndpoint;
	}

	public void setVideoIngestionEndpoint(String videoIngestionEndpoint) {
		this.videoIngestionEndpoint = videoIngestionEndpoint;
	}
	
	
}
