package com.example.usermanagement.config;

import com.example.usermanagement.interceptor.RequestTrackingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//https://www.tutorialspoint.com/spring_boot/spring_boot_interceptor.htm

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestTrackingInterceptor statisticsInterceptor;

    // Registers the custom interceptor to track and log HTTP requests
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(statisticsInterceptor); // adds the interceptor to the interceptor registry
    }
}
