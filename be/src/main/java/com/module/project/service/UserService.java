package com.module.project.service;

import org.springframework.stereotype.Service;

import com.module.project.model.Token;
import com.module.project.model.User;
import com.module.project.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenRepository tokenRepository;

    public User getUserInfo(String headerToken) {
        Token token = tokenRepository.findByToken(headerToken.replace("Bearer ", "")).filter(a -> !a.isExpired())
                .filter(a -> !a.isRevoked()).get();
        return token.getUser();
    }
}
