package org.example.core.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // اجازه CORS برای تمام مسیرهای API
                .allowedOrigins("http://localhost:4200") // اجازه درخواست‌ها از سرور Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // متدهای HTTP مجاز
                .allowedHeaders("*") // تمام هدرها مجاز
                .allowCredentials(true); // اجازه ارسال کوکی‌ها (مثلاً برای سشن‌ها اگر استفاده شود)
    }
}