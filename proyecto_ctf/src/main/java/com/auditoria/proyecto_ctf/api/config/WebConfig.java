package com.auditoria.proyecto_ctf.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuración para servir imágenes desde 'uploaded_images'
        registry.addResourceHandler("/uploaded_images/**")
                .addResourceLocations("file:uploaded_images/")
                .setCachePeriod(3600); // Habilitar la caché (opcional)

        // Serve uploaded files from 'tmp/uploadedImages' directory
        registry.addResourceHandler("/uploadedImages/**")
                .addResourceLocations("file:/tmp/uploadedImages/")
                .setCachePeriod(3600); // Enable caching (optional)
    }
}
