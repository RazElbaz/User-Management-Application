package com.example.usermanagement.service;

import com.example.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StatisticsService {

    private final UserRepository userRepository;

    // AtomicLong for thread-safe counters
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalRequestTime = new AtomicLong(0);

    public StatisticsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Cacheable method for retrieving the total number of users from the repository
    @Cacheable(value = "totalUsers", key = "'totalUsers'")
    public long getTotalUsers() {
        return userRepository.count();
    }

    // Records the request time (in nanoseconds) for performance tracking
    public void recordRequest(long requestTime) {
        totalRequests.incrementAndGet();
        totalRequestTime.addAndGet(requestTime);
        System.out.println("Request time recorded: " + requestTime + " ns");

    }

    // Retrieves the total number of requests processed
    public long getTotalRequests() {
        return totalRequests.get();
    }

    // Calculates the average request time (in nanoseconds)
    public double getAverageRequestTime() {
        long requests = totalRequests.get();
        double average = requests == 0 ? 0 : totalRequestTime.get() / (double) requests;
        System.out.println("Calculating average request time: totalRequestTime=" + totalRequestTime.get() + " ns, totalRequests=" + requests + ", average=" + average + " ns");

        return totalRequestTime.get() / (double) requests;
    }
}
