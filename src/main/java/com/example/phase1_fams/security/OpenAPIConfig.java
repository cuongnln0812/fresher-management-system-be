package com.example.phase1_fams.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "FAMS API Specification",
                version = "v1"),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080/"
                ),
                @Server(
                        description = "Production HTTPS ENV",
                        url = "https://academyonline.site/"),
                @Server(
                        description = "Production HTTP ENV",
                        url = "http://academyonline.site/")
        },
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "JWT")

public class OpenAPIConfig {

}
