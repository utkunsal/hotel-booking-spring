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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> register(RegisterRequest request) {
        return register(request, Role.ROLE_USER, false);
    }

    public ResponseEntity<?> register(RegisterRequest request, Role role, boolean verified) {
        // check if email, password and name is valid
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name, email or password cannot be empty!");
        }
        if (request.getName().length() < 3 || request.getName().length() > 40 || request.getPassword().length() < 3){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid name or password!");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (!pat.matcher(request.getEmail()).matches()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email!");
        }
        // check if email is used before
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Email is not available: " + request.getEmail());
        }
        // create user
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .verified(verified)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveToken(user, jwtToken);
        saveToken(user, refreshToken);
        return AuthenticationResponse.builder().token(jwtToken).refreshToken(refreshToken).name(user.getName()).build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {

        // get token
        String refreshToken = jwtService.getJwtRefreshFromCookie(request);

        // get email
        String email = null;
        try {
            email = jwtService.extractEmail(refreshToken);
        } catch (ExpiredJwtException | IllegalArgumentException ignored){}

        if (email != null) {

            var isTokenValid = tokenRepository.findByToken(refreshToken)
                    .map(t -> !t.isRevoked())
                    .orElse(false);

            var user = userRepository.findByEmail(email).orElse(null);
            if (user != null && jwtService.isTokenValid(refreshToken, user) && isTokenValid) {

                var accessToken = jwtService.generateToken(user);
                var newRefreshToken = jwtService.generateRefreshToken(user);
                revokeAllUserTokens(user);
                saveToken(user, accessToken);
                saveToken(user, newRefreshToken);
                return AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            }
        }
        return null;
    }

    private void saveToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

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
