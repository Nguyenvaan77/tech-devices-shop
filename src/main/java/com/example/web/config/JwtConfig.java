package com.example.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;

@Configuration
public class JwtConfig {

    @Bean
    public com.nimbusds.jwt.proc.ConfigurableJWTProcessor<SecurityContext> jwtProcessor() throws Exception {

        ConfigurableJWTProcessor<SecurityContext> processor =
                new DefaultJWTProcessor<>();

        // Google public key endpoint
        JWKSource<SecurityContext> jwkSource =
                new RemoteJWKSet<>(
                        new URL("https://www.googleapis.com/oauth2/v3/certs")
                );

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(
                        JWSAlgorithm.RS256,
                        jwkSource
                );

        processor.setJWSKeySelector(keySelector);

        return processor;
    }
}