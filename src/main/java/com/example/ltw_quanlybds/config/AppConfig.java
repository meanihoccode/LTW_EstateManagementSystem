package com.example.ltw_quanlybds.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    // Spring Security tự handle login/logout/phân quyền
    // Không cần Interceptor nữa
}
