package com.brightloop.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuditWebConfig implements WebMvcConfigurer {
    private final AuditTrailInterceptor trail;
    public AuditWebConfig(AuditTrailInterceptor trail){this.trail=trail;}
    @Override public void addInterceptors(InterceptorRegistry registry){registry.addInterceptor(trail).addPathPatterns("/api/**");}
}
