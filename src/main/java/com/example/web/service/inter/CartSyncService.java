package com.example.web.service.inter;

import org.springframework.stereotype.Service;

@Service
public interface CartSyncService {
    void syncDirtyCarts();
    void syncUserCart(Long userId);
}
