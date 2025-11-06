package org.example.gatewayservice.config;

import io.jsonwebtoken.Claims;
import org.example.gatewayservice.common.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
@RefreshScope
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired private RouterValidator routerValidator;
    @Autowired private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();

        if (!routerValidator.isSecured.test(request)) {
            return chain.filter(exchange); // public endpoint
        }

        var auth = request.getHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        var token = auth.substring(7).trim(); // cắt "Bearer "

        // nếu jwtUtil.validateToken(token) TRẢ VỀ true = hợp lệ
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "Invalid token");
        }

        Claims claims;
        try {
            claims = jwtUtil.getAllClaimsFromToken(token);
        } catch (Exception e) {
            return unauthorized(exchange, "Invalid token");
        }

        // add header & gắn lại vào exchange
        var mutatedReq = request.mutate()
                .header("X-User-Id", String.valueOf(claims.get("id")))
                .header("X-Role", String.valueOf(claims.get("role")))
                .build();

        return chain.filter(exchange.mutate().request(mutatedReq).build());
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
 *
 * Đây là một đoạn code để thực hiện xác thực JWT (JSON Web Token) trong Spring Cloud Gateway.
 * Nó kiểm tra xem có yêu cầu được bảo mật hay không và xác minh tính hợp lệ của token JWT trong tiêu đề yêu cầu.
 * Nếu token không hợp lệ hoặc thiếu, nó sẽ trả về lỗi UNAUTHORIZED (401) cho khách hàng.
 * Nếu token hợp lệ, nó sẽ lấy thông tin được giải mã từ token và thêm chúng vào tiêu đề của yêu cầu để sử dụng cho các mục đích khác.
 * **/