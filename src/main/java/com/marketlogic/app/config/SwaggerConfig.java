package com.marketlogic.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("!mock")
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Value("${app.version}")
    private String appVersion;

    @Bean
    public Docket appApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metaData())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.marketlogic.app"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Scheduler Management")
                .description("\"Scheduler Management REST API\"")
                .version(appVersion)
                .build();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
