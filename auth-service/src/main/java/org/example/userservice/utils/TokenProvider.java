package org.example.userservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.userservice.config.RsaKeyLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final RsaKeyLoader rsaKeyLoader;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityMs;

    @Value("${jwt.remember-me-expiration}")
    private long rememberMeValidityMs;

    private static final String AUTHORITIES_KEY = "roles";

    public String createToken(Authentication authentication,
                              boolean rememberMe,
                              String roles
                               ) {

        Instant now = Instant.now();
        long validity = rememberMe ? rememberMeValidityMs : accessTokenValidityMs;
        Instant expiry = now.plusMillis(validity);

        String username = authentication.getName();

        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, roles)   // "ROLE_USER,ROLE_ADMIN"
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(rsaKeyLoader.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }
}
