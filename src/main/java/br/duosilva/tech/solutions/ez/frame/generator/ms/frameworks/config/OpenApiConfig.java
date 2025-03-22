package br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
	
	@Bean
	OpenAPI customConfiguration() {
		return new OpenAPI().components(new Components())
				.info(new Info().title("ez-frame-generator-ms").description("DuoSilva Tech Solutions")
						.version("1.0.0-RELEASE")
						.license(new License().name("Apache License").url("https://github.com/ThaynaraDaSilva/ez-frame-generator-ms"))
						.termsOfService("https://github.com/ThaynaraDaSilva/ez-frame-generator-ms"))
				.addSecurityItem(new SecurityRequirement()
						.addList("Bearer Authentication"))
				.components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
	}
	
	private SecurityScheme createAPIKeyScheme() {
	    return new SecurityScheme().type(SecurityScheme.Type.HTTP)
	        .bearerFormat("JWT")
	        .scheme("bearer");
	}

    @Bean
    InternalResourceViewResolver defaultViewResolver() {
		return new InternalResourceViewResolver();
	}

}
