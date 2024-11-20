package com.example.usermanagement.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;
import org.springframework.stereotype.Service;

import java.time.Duration;

// https://www.baeldung.com/spring-bucket4j
@Service
public class RateLimiterService {

    private final Bucket bucket = createLoginBucket(); // In-memory bucket for rate limiting

    // For a rate limit of 10 requests per minute, we’ll create a bucket with capacity 5 and a refill rate of 5 tokens per minute
    private Bucket createLoginBucket() {
        Refill refill = Refill.intervally(5, Duration.ofMinutes(1)); // 5 tokens per minute
        Bandwidth bandwidth = Bandwidth.classic(5, refill); // Capacity of 5 tokens

        LocalBucketBuilder builder = Bucket.builder();// Use the new builder API to create the bucket
        return builder.addLimit(bandwidth).build();
    }

    public boolean isRequestAllowed() {
        return bucket.tryConsume(1); // Try to consume 1 token
    }

    public void resetLimit() {
        bucket.reset(); // Reset the tokens
    }
}
