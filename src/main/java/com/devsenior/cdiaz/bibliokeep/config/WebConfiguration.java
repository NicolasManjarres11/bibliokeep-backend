package com.devsenior.cdiaz.bibliokeep.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer{


    @Value("${app.upload.directory}")
    private String uploadDir;

    @Value("${app.upload.path-pattern}")
    private String pathPattern;

    @Value("${app.upload.cache-period}")
    private Integer cachePeriod;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       
        var uploadPath = Paths.get(uploadDir);
        var absolutePath = uploadPath.toFile().getAbsolutePath();

        registry.addResourceHandler(String.format("%s/**",pathPattern))
            .addResourceLocations(String.format("file:%s/", absolutePath))
            .setCachePeriod(cachePeriod);

    }


    
}
