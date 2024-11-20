package com.example.usermanagement.interceptor;

import com.example.usermanagement.service.StatisticsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//https://www.tutorialspoint.com/spring_boot/spring_boot_interceptor.htm
@Component
public class RequestTrackingInterceptor implements HandlerInterceptor {

    private final StatisticsService statisticsService;

    public RequestTrackingInterceptor(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // This method is invoked after the request has been processed
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestUri = request.getRequestURI();
        // Exclude requests to the /api/stats endpoint
        if (requestUri.equals("/api/stats")) {
            return;
        }

        long startTime = (long) request.getAttribute("startTime");
        long requestTime = System.nanoTime()  - startTime;

        statisticsService.recordRequest(requestTime);
    }

    // This method is invoked before the request is handled by a controller
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        // Exclude requests to the /api/stats endpoint from timing
        if (requestUri.equals("/api/stats")) {
            return true;
        }

        request.setAttribute("startTime", System.nanoTime());
        return true;
    }
}