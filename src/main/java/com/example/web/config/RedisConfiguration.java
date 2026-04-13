package com.example.web.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.web.dto.cart.response.CartResponse;
import com.example.web.dto.category.response.CategoryResponse;
import com.example.web.dto.product.response.ProductResponse;
import com.example.web.dto.user.response.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfiguration {

    // ObjectMapper dùng chung cho tất cả serializer
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // xử lý Java 8 Date/Time
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // Generic serializer dùng chung cho Object
    @Bean
    public Jackson2JsonRedisSerializer<Object> redisSerializer(ObjectMapper mapper) {
        JavaType javaType = mapper.getTypeFactory().constructType(Object.class);
        return new Jackson2JsonRedisSerializer<>(mapper, javaType);
    }

    // Serializer cho từng loại response
    private <T> Jackson2JsonRedisSerializer<T> createSerializer(ObjectMapper mapper, Class<T> type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        return new Jackson2JsonRedisSerializer<>(mapper, javaType);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper mapper) {

        // Cấu hình mặc định
        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer(mapper)))
                        .entryTtl(Duration.ofMinutes(10));

        // Serializer riêng cho từng cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("users",
                defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                createSerializer(mapper, UserResponse.class)
                        )
                ).entryTtl(Duration.ofMinutes(10))
        );

        cacheConfigurations.put("products",
                defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                createSerializer(mapper, ProductResponse.class)
                        )
                ).entryTtl(Duration.ofMinutes(30))
        );

        cacheConfigurations.put("categories",
                defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                createSerializer(mapper, CategoryResponse.class)
                        )
                ).entryTtl(Duration.ofHours(12))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public KeyGenerator redisKeyGenerator() {
        return (target, method, params) ->
                target.getClass().getSimpleName() + ":" +
                Arrays.stream(params)
                        .map(String::valueOf)
                        .collect(Collectors.joining(":"));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper mapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(redisSerializer(mapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(redisSerializer(mapper));
        template.afterPropertiesSet();
        return template;
    }
}