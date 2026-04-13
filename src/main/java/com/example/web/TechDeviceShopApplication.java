package com.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TechDeviceShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(TechDeviceShopApplication.class, args);
	}
}