package com.example.web.exception.redis;

import com.example.web.exception.AppException;

public class RedisException extends AppException{

    public RedisException(String message) {
        super(500,message);
        //TODO Auto-generated constructor stub
    }
}
