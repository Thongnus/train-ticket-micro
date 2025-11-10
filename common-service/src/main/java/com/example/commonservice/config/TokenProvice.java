package com.example.commonservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvice {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String ROLES = "roles";
    // Sử dụng @Value để cấu hình từ application-local.properties/yml
    @Value("${jwt.secret:gsdggfsdgddddddddgdsgdghsdrhhshsdghsdsghs}")
    private String secretKeyString;
    @Value("${jwt.access-token-expiration:2000000000}")
    private long accessTokenExpiration; // 1 phút (mặc định)

    private SecretKey getAccessSecretKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }


    // Tạo access token
    public String generateToken(Authentication authentication) {
        return generateToken(authentication, new HashMap<>());
    }

    // Tạo access token với custom claims
    public String generateToken(Authentication authentication, Map<String, Object> claims) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .claim("roles", authorities) // Thêm thông tin roles vào token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getAccessSecretKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractAllClaims(token);

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(ROLES).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .toList();

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // Trích xuất username từ access token
    public String extractUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    // Helper method để trích xuất claims từ access token
    public <T> T extractClaim(String token, ClaimResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Trích xuất tất cả claims từ access token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Kiểm tra token có hợp lệ không
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getAccessSecretKey())
                    .build()
                    .parseClaimsJws(authToken);
            return !isTokenExpired(authToken);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT token validation error: {}", e.getMessage());
            return false;
        }
    }

    // Kiểm tra token hết hạn chưa
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
//
//    // Kiểm tra refresh token có hợp lệ không
//    public boolean validateJwtRefreshToken(String authToken) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getRefreshSecretKey())
//                    .build()
//                    .parseClaimsJws(authToken);
//            return !isTokenExpiredRefresh(authToken);
//        } catch (JwtException | IllegalArgumentException e) {
//            log.error("JWT refresh token validation error: {}", e.getMessage());
//            return false;
//        }
//    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // Interface để resolve claims
    public interface ClaimResolver<T> {
        T apply(Claims claims);
    }

//    // Kiểm tra refresh token hết hạn chưa
//    public boolean isTokenExpiredRefresh(String token) {
//        Date expiration = extractClaimFromRefreshToken(token, Claims::getExpiration);
//        return expiration.before(new Date());
//    }
}