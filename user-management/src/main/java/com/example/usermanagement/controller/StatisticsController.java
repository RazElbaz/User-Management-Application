package com.example.usermanagement.controller;

import com.example.usermanagement.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//https://www.baeldung.com/java-initialize-hashmap
@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", statisticsService.getTotalUsers());
        stats.put("totalRequests", statisticsService.getTotalRequests());
        stats.put("averageRequestTime", statisticsService.getAverageRequestTime());
        return stats;
    }
}
