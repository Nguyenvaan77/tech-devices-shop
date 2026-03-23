// package com.example.web.security;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class GlobalCorsConfig implements WebMvcConfigurer {

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**") // Apply CORS policy to all endpoints
//                 .allowedOrigins("https://example.com", "https://example.com") // Allowed origins
//                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // HTTP methods
//                 .allowedHeaders("Authorization", "Content-Type","*") // Allowed headers
//                 .allowCredentials(true); // Allow credentials (cookies, Authorization headers, etc.)
//     }
// }
