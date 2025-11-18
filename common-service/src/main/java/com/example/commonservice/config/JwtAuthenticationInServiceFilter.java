package com.example.commonservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInServiceFilter extends OncePerRequestFilter {

    private final TokenProvice tokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        try {
            String path = request.getRequestURI();
            log.debug("JWT Filter - Processing: {} {}", request.getMethod(), path);

            String token = extractTokenFromRequest(request);

            if (token != null) {
                if (tokenProvider.validateToken(token)) {
                    Authentication authentication = tokenProvider.getAuthentication(token);

                    // 4) Set vào SecurityContext nếu chưa có
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Set authentication for user: {} with authorities: {}",
                                authentication.getName(),
                                authentication.getAuthorities());
                    }
                } else {
                    log.warn("Invalid JWT token for path: {}", path);
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.debug("No JWT token found in request for path: {}", path);
            }

        } catch (Exception ex) {
            // Clear context khi có lỗi
            log.error("Cannot set user authentication: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        // Tiếp tục filter chain
        chain.doFilter(request, response);
    }

    /**
     * Extract JWT token từ Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }

        return null;
    }
}