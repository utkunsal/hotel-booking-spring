package com.example.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.token.secret}")
    private String secretKey;
    @Value("${jwt.token.expiration}")
    private long jwtExpiration;
    @Value("${jwt.token.refresh-expiration}")
    private long jwtRefreshExpiration;



    private Claims extractAllClaims(String jwtToken){
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(jwtToken).getBody();
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String jwtToken){
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails){
        return createToken(userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails){
        return createToken(userDetails, jwtRefreshExpiration);
    }

    private String createToken(UserDetails userDetails, long jwtExpiration){
        return Jwts
                .builder()
                .setClaims(Collections.singletonMap("roles", userDetails.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority
                ).collect(Collectors.joining(" "))))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractEmail(jwtToken)) && !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Key getSignInKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
