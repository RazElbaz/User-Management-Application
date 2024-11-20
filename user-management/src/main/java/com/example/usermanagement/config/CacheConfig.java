package com.example.usermanagement.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

//https://docs.spring.io/spring-data/redis/reference/redis/template.html
@Configuration
@EnableCaching
public class CacheConfig {

    // Configures a RedisTemplate bean for interacting with Redis
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(org.springframework.data.redis.connection.RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer()); // Use String serializer for keys

        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // Use JSON serializer for values
        return template;
    }
}
