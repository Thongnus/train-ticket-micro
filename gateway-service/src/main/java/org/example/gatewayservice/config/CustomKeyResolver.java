package org.example.gatewayservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("customKeyResolver")
public class CustomKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        // Ưu tiên key theo user (đã được JWT GlobalFilter set)
        String uid = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (uid != null && !uid.isBlank()) {
            return Mono.just("uid:" + uid);
        }

        // Fallback: theo IP (ưu tiên X-Forwarded-For nếu có; nếu không lấy remoteAddress)
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // Lấy IP đầu tiên trong danh sách
            String firstIp = xff.split(",")[0].trim();
            return Mono.just("ip:" + firstIp);
        }

        var addr = exchange.getRequest().getRemoteAddress();
        String ip = (addr == null || addr.getAddress() == null)
                ? "unknown"
                : addr.getAddress().getHostAddress();
        return Mono.just("ip:" + ip);
    }
}
