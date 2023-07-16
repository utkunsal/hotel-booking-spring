package com.example.services;

import com.example.auth.AuthenticationRequest;
import com.example.auth.AuthenticationResponse;
import com.example.auth.RegisterRequest;
import com.example.entities.Token;
import com.example.repositories.TokenRepository;
import com.example.entities.Role;
import com.example.entities.User;
import com.example.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        return register(request, Role.ROLE_USER, false);
    }

    public AuthenticationResponse register(RegisterRequest request, Role role, boolean verified) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .verified(verified)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveToken(savedUser, jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).refreshToken(refreshToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveToken(user, jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).refreshToken(refreshToken).build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {

        // get token
        String refreshToken = jwtService.getJwtRefreshFromCookie(request);

        /*if (refreshToken == null){
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }
            refreshToken = authHeader.substring(7);
        }*/

        // get email
        String email = null;
        try {
            email = jwtService.extractEmail(refreshToken);
        } catch (ExpiredJwtException | IllegalArgumentException ignored){}

        if (email != null) {

            var user = userRepository.findByEmail(email).orElse(null);
            if (user != null && jwtService.isTokenValid(refreshToken, user)) {

                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveToken(user, accessToken);
                return AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        return null;
    }

    private void saveToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }
}
