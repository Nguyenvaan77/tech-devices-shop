package com.example.web.exception.redis;

public class RedisUnconnectedException extends RedisException{
    public RedisUnconnectedException(String message) {
        super(message);
    }
}
