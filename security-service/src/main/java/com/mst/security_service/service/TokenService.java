package com.mst.security_service.service;

import com.mst.security_service.dto.TokenDTO;
import com.mst.security_service.model.Permission;
import com.mst.security_service.model.Role;
import com.mst.security_service.model.UserCredentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class TokenService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiry.duration}")
    private Long expiryDuration;

    public TokenDTO generateToken(UserCredentials userCredentials){
        Map<String, Object> claims = new HashMap<>();
        Stream<String> userRoles = userCredentials.getRoles().stream().map(Role::getName);
        Stream<String> userPermissions = userCredentials.getPermissions().stream().map(Permission::getName);
        Set<String> roles = Stream.concat(userRoles, userPermissions).collect(Collectors.toSet());
        claims.put("id", userCredentials.getId());
        claims.put("email", userCredentials.getEmail());
        claims.put("roles",roles);

        String token = Jwts.builder()
                .claims(claims)
                .signWith(getSecretKey())
                .expiration(new Date(System.currentTimeMillis() + expiryDuration))
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer("alert_hub")
                .subject(userCredentials.getUsername())
                .compact();

        return new TokenDTO(token, "Bearer", expiryDuration, userCredentials.getUsername(), roles);
    }

    public Boolean isTokenValid(String token){
        Claims claims = extractAllClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.after(new Date());
    }

    public Long extractUserId(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("id", Long.class);
    }

    public List<SimpleGrantedAuthority> extractRoles(String token) {
        List<String> roles = extractAllClaims(token).get("roles", List.class);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey(){
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
