package com.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@EnableCaching
@SpringBootApplication
@EnableScheduling
public class TechDeviceShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(TechDeviceShopApplication.class, args);
	}
}

