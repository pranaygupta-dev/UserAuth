package com.example.UserAuth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void testOpenAPIBean_IsCreatedCorrectly() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SwaggerConfig.class);

        OpenAPI openAPI = context.getBean(OpenAPI.class);

        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("User Auth APIs", info.getTitle());
        assertEquals("User Auth APIs", info.getDescription());


        assertNotNull(openAPI.getServers());
        assertEquals(1, openAPI.getServers().size());
        Server server = openAPI.getServers().get(0);
        assertEquals("http://localhost:9090", server.getUrl());
        assertEquals("local", server.getDescription());


        assertNotNull(openAPI.getTags());
        assertEquals(2, openAPI.getTags().size());
        assertTrue(openAPI.getTags().stream().anyMatch(tag -> tag.getName().equals("Public APIs")));
        assertTrue(openAPI.getTags().stream().anyMatch(tag -> tag.getName().equals("User APIs")));


        assertNotNull(openAPI.getComponents());
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
        assertEquals("Authorization", securityScheme.getName());


        assertNotNull(openAPI.getSecurity());
        assertEquals(1, openAPI.getSecurity().size());
        SecurityRequirement requirement = openAPI.getSecurity().get(0);
        assertTrue(requirement.containsKey("bearerAuth"));


        context.close();
    }
}
