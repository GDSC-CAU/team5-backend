package org.gdsccau.team5.safebridge.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedMethods("*")
            .allowedHeaders("Authorization", "Content-Type")
            .allowedOrigins("*");
//            .allowedOrigins("http://localhost:3000")
//            .allowedOrigins("https://safebridge.site/");
    }
}
