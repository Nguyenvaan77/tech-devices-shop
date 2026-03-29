package com.example.web.service.imple;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.UserDetailsAwareConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.web.entity.User;
import com.example.web.service.inter.JwtService;
import com.example.web.util.TokenEnum;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-key}")
    private String refreshKey;

    @Value("${jwt.reset-password-key}")
    private String resetPasswordKey;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.expiryHour}")
    private long expiryHour;

    @Override
    public String generateAccessToken(UserDetails user) {
        return generateAccessToken(new HashMap<>(), user);
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return generateRefreshToken(new HashMap<>(), user);
    }

    @Override
    public String generateResetPasswordToken(UserDetails user) {
        return generateResetPasswordToken(new HashMap<>(), user);
    }

    @Override
    public String extractUsername(String token, TokenEnum tokenEnum) {
        return extractClaim(token, tokenEnum, Claims::getSubject);
    }

    @Override
    public Date extractTokenExpired(String token, TokenEnum tokenEnum) {
        return extractClaim(token, tokenEnum, Claims::getExpiration);
    }

    @Override
    public boolean isValid(String token, TokenEnum tokenEnum, UserDetails userDetails) {
        final String username = extractUsername(token, tokenEnum);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, tokenEnum));
    }

    private boolean isTokenExpired(String token, TokenEnum tokenEnum) {
        return extractTokenExpired(token, tokenEnum).before(new Date());
    }

    private String generateAccessToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour))
                .signWith(getKey(TokenEnum.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(TokenEnum.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateResetPasswordToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(TokenEnum.RESET_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenEnum tokenEnum) {
        byte[] keyBytes;

        switch (tokenEnum) {
            case ACCESS_TOKEN:
                keyBytes = Decoders.BASE64.decode(secretKey);
                break;
            case REFRESH_TOKEN:
                keyBytes = Decoders.BASE64.decode(refreshKey);
                break;
            case RESET_TOKEN:
                keyBytes = Decoders.BASE64.decode(resetPasswordKey);
                break;

            default:
                throw new IllegalArgumentException("Invalid token type");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, TokenEnum tokenEnum, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaim(token, tokenEnum);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaim(String token, TokenEnum tokenEnum) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(tokenEnum))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
