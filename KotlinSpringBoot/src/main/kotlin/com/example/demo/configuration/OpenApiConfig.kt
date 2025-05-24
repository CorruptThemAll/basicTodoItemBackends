package com.example.demo.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

	@Bean
	fun customOpenAPI(): OpenAPI {
		return OpenAPI()
			.info(Info().title("API Documentation").version("1.0").description("API with JWT Authentication"))
			.addSecurityItem(SecurityRequirement().addList("bearerAuth"))
			.components(
				Components()
					.addSecuritySchemes(
						"bearerAuth",
						SecurityScheme()
							.name("bearerAuth")
							.type(SecurityScheme.Type.HTTP)
							.scheme("bearer")
							.bearerFormat("JWT")
					)
			)
	}
}