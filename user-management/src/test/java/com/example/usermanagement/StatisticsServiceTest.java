package com.example.usermanagement;


import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTotalUsers() {
        // Given
        when(userRepository.count()).thenReturn(5L);

        // When
        long totalUsers = statisticsService.getTotalUsers();

        // Then
        assertEquals(5, totalUsers);
    }

    @Test
    void testRecordRequest() {
        // Given
        long requestTime = 500000L;

        // When
        statisticsService.recordRequest(requestTime);

        // Then
        assertEquals(1, statisticsService.getTotalRequests());
        assertEquals(requestTime, statisticsService.getAverageRequestTime());
    }

    @Test
    void testGetAverageRequestTime() {
        // Given
        statisticsService.recordRequest(1000L);
        statisticsService.recordRequest(3000L);

        // When
        double average = statisticsService.getAverageRequestTime();

        // Then
        assertEquals(2000.0, average);
    }

    @Test
    void testRecordMultipleRequests() {
        // Given
        long requestTime1 = 1000L;
        long requestTime2 = 2000L;
        long requestTime3 = 3000L;

        // When
        statisticsService.recordRequest(requestTime1);
        statisticsService.recordRequest(requestTime2);
        statisticsService.recordRequest(requestTime3);

        // Then
        assertEquals(3, statisticsService.getTotalRequests());
        assertEquals((requestTime1 + requestTime2 + requestTime3) / 3.0, statisticsService.getAverageRequestTime());
    }
    @Test
    void testGetAverageRequestTime_NoRequests() {
        // When
        double average = statisticsService.getAverageRequestTime();

        // Then
        assertEquals(NaN, average, "Average request time should be 0 when no requests are recorded.");
    }

    @Test
    void testRecordLargeRequestTimes() {
        // Given
        long largeRequestTime = Long.MAX_VALUE;

        // When
        statisticsService.recordRequest(largeRequestTime);
        statisticsService.recordRequest(largeRequestTime);

        // Then
        assertEquals(2, statisticsService.getTotalRequests());
    }
    @Test
    void testGetTotalRequests_InitiallyZero() {
        // When
        long totalRequests = statisticsService.getTotalRequests();

        // Then
        assertEquals(0, totalRequests, "Total requests should be 0 before any requests are recorded.");
    }

    @Test
    void testGetTotalUsers_NoUsers() {
        // Given
        when(userRepository.count()).thenReturn(0L);

        // When
        long totalUsers = statisticsService.getTotalUsers();

        // Then
        assertEquals(0, totalUsers);
    }
    @Test
    void testRecordRequest_MaxLongValue() {
        // Given
        long maxTime = Long.MAX_VALUE;

        // When
        statisticsService.recordRequest(maxTime);

        // Then
        assertEquals(1, statisticsService.getTotalRequests());
        assertEquals((double) maxTime, statisticsService.getAverageRequestTime());
    }
    @Test
    void testGetTotalUsers_RepositoryThrowsException() {
        // Given
        when(userRepository.count()).thenThrow(new RuntimeException("Database error"));

        // Then
        assertThrows(RuntimeException.class, () -> statisticsService.getTotalUsers());
    }


}
