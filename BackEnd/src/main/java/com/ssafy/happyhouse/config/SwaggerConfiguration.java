package com.ssafy.happyhouse.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	    info = @Info(
	        title = "HppayHouse API 명세서",
	        description = "<h3>HppayHouse API Reference for Developers</h3>Swagger를 이용한 VUE API<br>",
	        version = "v1",
	        contact = @Contact(
	            name = "hissam",
	            email = "hissam@ssafy.com",
	            url = "http://edu.ssafy.com"
	        )
	    )
	)
@Configuration
public class SwaggerConfiguration {

	@Bean
	public GroupedOpenApi mapApi() {
		return GroupedOpenApi.builder().group("house").pathsToMatch("/house/**").build();
	}
	
	
	
}
