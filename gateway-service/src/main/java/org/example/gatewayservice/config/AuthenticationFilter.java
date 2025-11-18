package org.example.gatewayservice.config;

import com.example.commonservice.config.TokenProvice;
import com.example.commonservice.entity.ErrorResponse;
import com.example.commonservice.exceptionHandle.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway Authentication Filter
 *
 * Nhiệm vụ:
 * 1. Kiểm tra endpoint có cần authentication không (public/secured)
 * 2. Validate JWT token (signature + expiration)
 * 3. Gọi Auth Service để verify token còn active (chưa bị revoke/lock)
 * 4. Forward token gốc đến downstream service
 *
 * LƯU Ý:
 * - Gateway KHÔNG parse claims và KHÔNG gửi thêm headers (X-User-Id, X-Role...)
 * - Downstream service tự validate token và extract claims từ JWT
 * - Đảm bảo an toàn vì không ai có thể fake headers
 */
@Slf4j
@Component
@RefreshScope
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final RouterValidator routerValidator;
    private final TokenProvice jwtUtil;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${clientService.authentication-service-url}")
    private String authServiceUrl;

    public AuthenticationFilter(RouterValidator routerValidator,
                                TokenProvice jwtUtil,
                                WebClient.Builder webClientBuilder) {
        this.routerValidator = routerValidator;
        this.jwtUtil = jwtUtil;
        this.webClientBuilder = webClientBuilder;

        // Khởi tạo ObjectMapper với JavaTimeModule
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().toString();

        log.debug("Gateway Filter - {} {}", method, path);

        // 1) Kiểm tra public endpoint
        if (!routerValidator.isSecured.test(request)) {
            log.debug("Public endpoint, skip authentication");
            return chain.filter(exchange);
        }

        // 2) Lấy Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for: {} {}", method, path);
            return sendUnauthorizedResponse(
                    exchange,
                    ErrorCode.UNAUTHORIZED,
                    "Missing or invalid Authorization header"
            );
        }

        String token = authHeader.substring(7).trim();

        // 3) Validate JWT locally (signature + expiration)
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token validation failed for: {} {}", method, path);
            return sendUnauthorizedResponse(
                    exchange,
                    ErrorCode.TOKEN_INVALID,
                    "Invalid or expired token"
            );
        }

        // 4) Extract username để gọi Auth Service (optional - có thể bỏ nếu Auth Service tự parse token)
        String username;
        try {
            Claims claims = jwtUtil.getAllClaimsFromToken(token);
            username = claims.getSubject();
            log.debug("Token validated for user: {}", username);
        } catch (Exception e) {
            log.error("Error extracting claims from token", e);
            return sendUnauthorizedResponse(
                    exchange,
                    ErrorCode.TOKEN_INVALID,
                    "Cannot parse token"
            );
        }

        // 5) Gọi Auth Service để verify token còn active (chưa bị revoke)
        Mono<Void> validateWithAuthService = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(authServiceUrl + "/api/validate-token")
                        .queryParam("username", username)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.warn("Auth service validation failed for user {}: {}", username, body);
                                    return Mono.error(new RuntimeException("Token validation failed"));
                                })
                )
                .toBodilessEntity()
                .then();

        // 6) Forward request với token gốc (KHÔNG thêm headers mới)
        return validateWithAuthService
                .then(Mono.defer(() -> {
                    log.debug("Token validated successfully for user: {}, forwarding request", username);

                    // GIỮ NGUYÊN request, chỉ forward token gốc
                    // Downstream service sẽ tự validate và extract claims
                    return chain.filter(exchange);
                }))
                .onErrorResume(ex -> {
                    log.error("Authentication failed for user {}: {}", username, ex.getMessage());
                    return sendUnauthorizedResponse(
                            exchange,
                            ErrorCode.UNAUTHORIZED,
                            "Authentication failed"
                    );
                });
    }

    /**
     * Gửi response lỗi UNAUTHORIZED
     * mono chi nhan byte data nen can convert sang DataBuffer
     */

    private Mono<Void> sendUnauthorizedResponse(
            ServerWebExchange exchange,
            ErrorCode errorCode,
            String customMessage) {

        ServerHttpResponse response = exchange.getResponse();

        // Kiểm tra response đã được commit chưa
        if (response.isCommitted()) {
            log.warn("Response already committed, cannot send error response");
            return Mono.empty();
        }


        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getStatusValue(),
                errorCode.getCode(),
                customMessage != null ? customMessage : errorCode.getMessage(),
                exchange.getRequest().getPath().value()
        );

        // Serialize ErrorResponse thành JSON
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            // Fallback response nếu serialize lỗi
            String fallback = String.format(
                    "{\"status\":%d,\"success\":false,\"code\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                    errorCode.getStatusValue(),
                    errorCode.getCode(),
                    "Authentication failed",
                    exchange.getRequest().getPath().value()
            );
            bytes = fallback.getBytes();
        }

        // Set response status và headers
        response.setStatusCode(HttpStatus.valueOf(errorCode.getStatusValue()));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().setContentLength(bytes.length);

        // Write response body
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // Chạy sớm nhất
    }
}