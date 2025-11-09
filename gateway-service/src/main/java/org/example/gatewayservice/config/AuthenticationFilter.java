package org.example.gatewayservice.config;

import io.jsonwebtoken.Claims;
import org.example.gatewayservice.common.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final RouterValidator routerValidator;
    private final JwtUtil jwtUtil;
    private final WebClient.Builder webClientBuilder;

    @Value("${clientService.authentication-service-url}")
    private String authServiceUrl; // ví dụ: http://auth-service:8080

    public AuthenticationFilter(RouterValidator routerValidator,
                                JwtUtil jwtUtil,
                                WebClient.Builder webClientBuilder) {
        this.routerValidator = routerValidator;
        this.jwtUtil = jwtUtil;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 0) Whitelist các endpoint public
        if (!routerValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }

        // 1) Lấy & kiểm tra Authorization header
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }
        String token = auth.substring(7).trim();

        // 2) Verify chữ ký + hạn token (cục bộ)
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "Invalid token");
        }

        // 3) Parse claims để lấy thông tin cần dùng (vd: subject/username, id, role...)
        final Claims claims;
        try {
            claims = jwtUtil.getAllClaimsFromToken(token);
        } catch (Exception e) {
            return unauthorized(exchange, "Invalid token");
        }
        final String username = claims.getSubject();

        // (Tuỳ chọn) Lấy thêm id/role từ claims nếu bạn có nhúng vào JWT
      //  String username = jwtUtil.extractUserNameFromToken(token);
        String userId = claims.get("id", String.class);
        String role   = claims.get("role", String.class);

        // 4) Gọi Auth-Service xác minh token còn "tồn tại" (chưa bị revoke/lock)
        //    GET {authServiceUrl}/api/validate-token?username=<username>
        Mono<Void> validateWithAuthService = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(authServiceUrl + "/api/validate-token")
                        .queryParam("username", username)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(), resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Unauthorized: " + body)))
                )
                .toBodilessEntity()
                .then();

        // 5) Nếu Auth-Service OK → gắn header phụ (nếu muốn) rồi forward
        return validateWithAuthService.then(Mono.defer(() -> {
                    ServerHttpRequest mutatedReq = request.mutate()
                            // GIỮ NGUYÊN Authorization để downstream có thể tự verify thêm nếu cần
                            .header("X-User-Id", userId == null ? "" : userId)
                            .header("X-Role", role == null ? "" : role)
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedReq).build());
                }))
                // 6) Nếu Auth-Service báo lỗi → trả 401
                .onErrorResume(ex -> unauthorized(exchange, "Unauthorized"));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        var res = exchange.getResponse();
        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        return res.setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // chạy sớm
    }
}
/**
 * Đây là một đoạn code để thực hiện xác thực JWT (JSON Web Token) trong Spring Cloud Gateway.
 * Nó kiểm tra xem có yêu cầu được bảo mật hay không và xác minh tính hợp lệ của token JWT trong tiêu đề yêu cầu.
 * Nếu token không hợp lệ hoặc thiếu, nó sẽ trả về lỗi UNAUTHORIZED (401) cho khách hàng.
 * Nếu token hợp lệ, nó sẽ lấy thông tin được giải mã từ token và thêm chúng vào tiêu đề của yêu cầu để sử dụng cho các mục đích khác.
 **/