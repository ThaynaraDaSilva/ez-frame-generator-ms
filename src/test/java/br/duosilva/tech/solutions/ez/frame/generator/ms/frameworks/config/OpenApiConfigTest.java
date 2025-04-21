package br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void shouldConfigureOpenAPIWithCorrectProperties() {
        // Act
        OpenAPI openAPI = openApiConfig.customConfiguration();

        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("ez-frame-generator-ms", openAPI.getInfo().getTitle());
        assertEquals("DuoSilva Tech Solutions", openAPI.getInfo().getDescription());
        assertEquals("1.0.0-RELEASE", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("Apache License", openAPI.getInfo().getLicense().getName());
        assertEquals("https://github.com/ThaynaraDaSilva/ez-frame-generator-ms", openAPI.getInfo().getLicense().getUrl());
        assertEquals("https://github.com/ThaynaraDaSilva/ez-frame-generator-ms", openAPI.getInfo().getTermsOfService());

        // Verify security requirements
        assertNotNull(openAPI.getSecurity());
        assertEquals(1, openAPI.getSecurity().size());
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertTrue(securityRequirement.containsKey("Bearer Authentication"));

        // Verify security scheme
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
    }

    @Test
    void shouldConfigureInternalResourceViewResolver() {
        // Act
        InternalResourceViewResolver viewResolver = openApiConfig.defaultViewResolver();

        // Assert
        assertNotNull(viewResolver);
        assertTrue(viewResolver instanceof InternalResourceViewResolver);
    }
}