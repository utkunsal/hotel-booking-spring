package com.example.services;

import com.example.entities.User;
import com.example.repositories.TokenRepository;
import com.example.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // revoke tokens
        String token = jwtService.getJwtAccessFromCookie(request);
        Optional<User> user = userRepository.findByEmail(jwtService.extractEmail(token));
        user.ifPresent(this::revokeAllUserTokens);
        // clear context
        SecurityContextHolder.clearContext();
    }

    /*private void revokeTokens(HttpServletRequest request) {
        // get tokens
        String accessToken = jwtService.getJwtAccessFromCookie(request);
        String refreshToken = jwtService.getJwtRefreshFromCookie(request);
        // revoke and save
        List<Token> tokens = new ArrayList<>();
        tokenRepository.findByToken(accessToken).ifPresent(tokens::add);
        tokenRepository.findByToken(refreshToken).ifPresent(tokens::add);
        if (!tokens.isEmpty()) {
            tokens.forEach(token -> {
                token.setRevoked(true);
            });
            tokenRepository.saveAll(tokens);
        }
    }*/

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }
}
