package com.example.commonservice.config;

import com.example.commonservice.util.AuthHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        var userId = req.getHeader(com.example.commonservice.util.AuthHeaders.USER_ID);
        var roles  = req.getHeader(AuthHeaders.ROLES);

        if (userId != null) {
            var authorities = roles == null ? 
                    java.util.List.<SimpleGrantedAuthority>of() :
                    Arrays.stream(roles.split(","))
                            .filter(s -> !s.isBlank())
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
                            .toList();

            var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}
