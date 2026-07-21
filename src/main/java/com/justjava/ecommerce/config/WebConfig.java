package com.justjava.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:./uploads/products}")
    private String uploadDir;

    @Value("${app.vendor-upload-dir:./uploads/vendors}")
    private String vendorUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String productsPath = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(productsPath);

        String vendorsPath = Paths.get(vendorUploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/vendors/**")
                .addResourceLocations(vendorsPath);
    }
}
