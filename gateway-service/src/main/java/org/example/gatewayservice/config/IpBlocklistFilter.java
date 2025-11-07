package org.example.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class IpBlocklistFilter implements GlobalFilter, Ordered {

    // ENV ví dụ: GATEWAY_BLOCK_IPS="203.0.113.8,198.51.100.0/24,10.0.0.0/8"
    private static  List<String> denied = List.of("203.0.113.8,198.51.100.0"); // IPv4 + IPv6 localhost

//    public IpBlocklistFilter(@Value("${GATEWAY_BLOCK_IPS:}") String blocked) {
//        if (blocked == null || blocked.isBlank()) {
//            this.BLOCKED_IPS = List.of();
//        } else {
//            this.BLOCKED_IPS = Arrays.stream(blocked.split("\\s*,\\s*"))
//                    .filter(s -> !s.isBlank())
//                    .map(IpAddressMatcher::new)        // hỗ trợ IP đơn & CIDR
//                    .toList();
//        }
//    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (denied.isEmpty()) return chain.filter(exchange);

        String clientIp = extractClientIp(exchange);
        if (clientIp == null) return chain.filter(exchange);

        boolean blocked = denied.stream().anyMatch(m -> m.matches(clientIp));
        if (!blocked) return chain.filter(exchange);

        var res = exchange.getResponse();
        res.setStatusCode(HttpStatus.FORBIDDEN);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        var body = ("{\"error\":\"forbidden\",\"reason\":\"ip_blocked\",\"ip\":\"" + clientIp + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return res.writeWith(Mono.just(res.bufferFactory().wrap(body)));
    }

    private String extractClientIp(ServerWebExchange exchange) {
        var headers = exchange.getRequest().getHeaders();
        // Ưu tiên X-Forwarded-For (lấy IP đầu tiên)
        String xff = headers.getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        var addr = exchange.getRequest().getRemoteAddress();
        return (addr == null || addr.getAddress() == null) ? null : addr.getAddress().getHostAddress();
    }

    @Override public int getOrder() { return -200; } // trước JWT
}
