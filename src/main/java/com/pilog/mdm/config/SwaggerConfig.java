package com.pilog.mdm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableSwagger2
@Configuration
public class SwaggerConfig extends WebMvcConfigurationSupport {
	public static final String AUTHORIZATION_HEADER = "Authorization";

	private ApiKey apiKeys() {
		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
	}

	private List<SecurityContext> securityContexts() {
		return Arrays.asList(SecurityContext.builder().securityReferences(sf()).build());
	}

	private List<SecurityReference> sf() {

		AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");

		return Arrays.asList(new SecurityReference("JWT", new AuthorizationScope[] { scope }));
	}

	@Bean
	public Docket api() {

		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.pilog.mdm"))
				.paths(PathSelectors.any()).build().pathMapping("/").apiInfo(metaData()).securityContexts(securityContexts())
				.securitySchemes(Arrays.asList(apiKeys()));
	}

	private ApiInfo metaData() {

		Contact contact = new Contact("Pilog Group", "https://www.piloggroup.com",
				"hr@piloggroup.com");

		return new ApiInfo("Pilog Group",
				"APIs for Pilog SmartBi",
				"apiVersion",
				"Terms of Service: Need to Update",
				contact,
				"Copyright © 2023 PiLog Group",
				"insights",
				new ArrayList<>());
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}

//@Configuration
//public class SwaggerConfig {
//
//	public static final String AUTHORIZATION_HEADER = "Authorization";
//
//	private ApiKey apiKeys() {
//		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
//	}
//
//	private List<SecurityContext> securityContexts() {
//		return Arrays.asList(SecurityContext.builder().securityReferences(sf()).build());
//	}
//
//	private List<SecurityReference> sf() {
//
//		AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");
//
//		return Arrays.asList(new SecurityReference("JWT", new AuthorizationScope[] { scope }));
//	}
//
//	@Bean
//	public Docket api() {
//
//		return new Docket(DocumentationType.SWAGGER_2).apiInfo(getInfo()).securityContexts(securityContexts())
//				.securitySchemes(Arrays.asList(apiKeys())).select().apis(RequestHandlerSelectors.any())
//				.paths(PathSelectors.any()).build();
//
//	}
//
//	private ApiInfo getInfo() {
//
//
//		Contact contact = new Contact("Pilog Group", "https://www.piloggroup.com",
//				"hr@piloggroup.com");
//
//		return new ApiInfo("Pilog Group",
//				"APIs for Pilog SmartBi",
//				"apiVersion",
//				"Terms of Service: Need to Update",
//				contact,
//				"Copyright © 2023 PiLog Group",
//				"insights",
//				new ArrayList<>());
//	};
//
//}