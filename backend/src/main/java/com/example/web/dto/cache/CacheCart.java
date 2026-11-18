package com.example.web.dto.cache;

import java.util.HashMap;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CacheCart {
    private Long userId;
    private HashMap<String, Integer> productAndQuantity;
}