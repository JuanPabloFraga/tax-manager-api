package com.taxmanager.taxmanagerapi.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc / OpenAPI configuration.
 *
 * <p>Adds project metadata to the Swagger UI available at {@code /swagger-ui.html},
 * and configures the global Bearer token security scheme so authenticated
 * endpoints show the lock icon.</p>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI taxManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tax Manager API")
                        .description("API REST de gestión fiscal argentina para contadores independientes. "
                                + "Permite administrar contribuyentes, comprobantes fiscales y generar libros IVA.")
                        .version("0.1.0")
                        .contact(new Contact()
                                .name("Tax Manager")
                                .url("https://github.com/tax-manager/tax-manager-api"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Access token JWT. Obtenerlo vía POST /api/v1/auth/login")));
    }
}
