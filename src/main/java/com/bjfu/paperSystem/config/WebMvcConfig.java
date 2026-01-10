package com.bjfu.paperSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将/upload路径映射到实际的文件存储目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/java_uploads/");
    }
}