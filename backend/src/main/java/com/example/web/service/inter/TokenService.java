package com.example.web.service.inter;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.web.entity.Token;
import com.example.web.entity.User;

public interface TokenService {
    Token provideNewAccessAndRefreshTokenForUser(User user);

    Token findByUserId(Long id);

     void deleteByUser(User user);

    Token save(Token token);

    Token create(Token token);
}
