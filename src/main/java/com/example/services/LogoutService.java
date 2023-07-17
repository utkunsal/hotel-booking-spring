package com.example.services;

import com.example.entities.Token;
import com.example.entities.User;
import com.example.repositories.TokenRepository;
import com.example.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // revoke tokens
        revokeTokens(request);
        // clear context
        SecurityContextHolder.clearContext();
    }

    private void revokeTokens(HttpServletRequest request) {
        // get tokens
        String accessToken = jwtService.getJwtAccessFromCookie(request);
        String refreshToken = jwtService.getJwtRefreshFromCookie(request);
        // revoke and save
        List<Token> tokens = new ArrayList<>();
        tokenRepository.findByToken(accessToken).ifPresent(tokens::add);
        tokenRepository.findByToken(refreshToken).ifPresent(tokens::add);
        if (!tokens.isEmpty()) {
            tokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(tokens);
        }
    }
}
