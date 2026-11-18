package com.example.web.scheduler;

import com.example.web.service.inter.CartSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartSyncScheduler {

    private final CartSyncService cartSyncService;

    // 3 phút gọi batch job đồng bộ từ redis xuống mysql 1 lần
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void scheduleCartSync() {
        log.info("Scheduler: Bắt đầu gọi service đồng bộ Cart.");
        cartSyncService.syncDirtyCarts();
    }
}
