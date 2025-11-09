package org.example.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Component
public class IpBlocklistFilter implements GlobalFilter, Ordered {

    // Ví dụ cấu hình:
    // GATEWAY_BLOCK_IPS="203.0.113.8,198.51.100.0/24,10.0.0.0/8,::1"
    private final List<IpAddressMatcher> blockedMatchers;

    public IpBlocklistFilter(@Value("${GATEWAY_BLOCK_IPS:}") String blocked) {
        if (blocked == null || blocked.isBlank()) {
            this.blockedMatchers = List.of();
        } else {
            this.blockedMatchers = Arrays.stream(blocked.split("\\s*,\\s*"))
                    .filter(s -> !s.isBlank())
                    .map(IpAddressMatcher::new) // hỗ trợ IP đơn & CIDR & IPv6
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (blockedMatchers.isEmpty()) return chain.filter(exchange);

        String clientIp = extractClientIp(exchange);
        if (clientIp == null || clientIp.isBlank()) return chain.filter(exchange);

        boolean blocked = blockedMatchers.stream().anyMatch(m -> m.matches(clientIp));
        if (!blocked) return chain.filter(exchange);

        var res = exchange.getResponse();
        res.setStatusCode(HttpStatus.FORBIDDEN);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        var body = ("{\"error\":\"forbidden\",\"reason\":\"ip_blocked\",\"ip\":\"" + clientIp + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return res.writeWith(Mono.just(res.bufferFactory().wrap(body)));
    }

    private String extractClientIp(ServerWebExchange exchange) {
        var req = exchange.getRequest();
        var headers = req.getHeaders();

        // Ưu tiên X-Real-IP do proxy nội bộ set
        String ip = headers.getFirst("X-Real-IP");
        if (ip != null && !ip.isBlank()) return ip.trim();

        // Sau đó tới X-Forwarded-For (lấy IP đầu tiên)
        String xff = headers.getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();

        var addr = req.getRemoteAddress();
        return (addr == null || addr.getAddress() == null) ? null : addr.getAddress().getHostAddress();
    }

    // Chạy rất sớm (trước JWT filter của bạn đang -1)
    @Override public int getOrder() { return -200; }
}
