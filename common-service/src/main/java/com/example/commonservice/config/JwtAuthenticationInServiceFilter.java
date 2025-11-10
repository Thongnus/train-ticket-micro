package com.example.commonservice.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter “chuẩn” cho mọi microservice:
 * - Đọc Bearer token từ Authorization header
 * - Validate token (chữ ký + hạn)
 * - Trích username/roles/... từ claims
 * - Set Authentication vào SecurityContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInServiceFilter extends OncePerRequestFilter {

    private final TokenProvice tokenProvice;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        try {
            log.debug("--Vao FILTER AUTHENNN------");
            String bearer = req.getHeader("Authorization");
            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
                String token = bearer.substring(7).trim();

                if (tokenProvice.validateJwtToken(token)) {
                    Authentication authentication = tokenProvice.getAuthentication(token);
                    if(SecurityContextHolder.getContext().getAuthentication() == null)
                    {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Set authentication for user: {}", authentication.getName());
                    }

                }
            }
        } catch (Exception ex) {
            // Không ném ra ngoài để ExceptionTranslationFilter xử lý; có thể log nếu cần
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(req, res);
    }

    private List<SimpleGrantedAuthority> parseAuthorities(String rolesStr) {
        if (!StringUtils.hasText(rolesStr)) return List.of();
        return Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::normalizeRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Chuẩn hoá: nếu chưa có prefix ROLE_ thì thêm vào để dùng hasRole/hasAuthority nhất quán
     */
    private String normalizeRole(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
