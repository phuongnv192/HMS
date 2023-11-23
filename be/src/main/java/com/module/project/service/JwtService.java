package com.module.project.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import com.module.project.dto.ClaimEnum;
import com.module.project.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(ClaimEnum.USER_ID.name, user.getId().toString());
        extraClaims.put(ClaimEnum.ROLE_ID.name, user.getRole().getId().toString());
        extraClaims.put(ClaimEnum.ROLE_NAME.name, user.getRole().getName());
        return generateToken(extraClaims, user);
    }

    private String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts.builder().claims(extraClaims).subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * jwtExpiration))
                .signWith(getSignKey()).compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsernameOrEmail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignKey() {
        byte[] ketBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(ketBytes);
    }

    public String extractUsernameOrEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    };

    public String extractByName(String token, String name) {
        final Claims claims = extractAllClaims(token);
        return claims.get(name, String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> clamsResolve) {
        final Claims claims = extractAllClaims(token);
        return clamsResolve.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) getSignKey()).build().parseSignedClaims(token).getPayload();
    }

}
