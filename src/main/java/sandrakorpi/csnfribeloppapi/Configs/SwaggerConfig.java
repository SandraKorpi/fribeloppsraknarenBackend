package sandrakorpi.csnfribeloppapi.Configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Your API Title", version = "v1"))
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info() // Fullständig väg för Info
                        .title("CSNfribelopp")
                        .version("1.0")
                        .description("API för appen CSNfribelopp"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new io.swagger.v3.oas.models.security.SecurityScheme() // Fullständig väg för SecurityScheme
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP) // Fullständig väg för Type
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ange JWT-token här.")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
