package com.example.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web.entity.Token;
import com.example.web.entity.User;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>{
    Optional<Token> findByUserId(Long userId);
    Optional<Token> findByUser(User user);
}
