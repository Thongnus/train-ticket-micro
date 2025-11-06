//package org.example.gatewayservice.config;
//
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Mono;
///* xac dinh user or ip nao gui request */
//@Configuration
//public class RateLimitConfig {
//    @Bean("CustomKeyResolver")
//    public KeyResolver userKeyResolver() {
//        return exchange -> {
//            var uid = exchange.getRequest().getHeaders().getFirst("X-User-Id");
//            if (uid != null && !uid.isBlank()) return Mono.just("uid:" + uid);
//            var ip = exchange.getRequest().getRemoteAddress();
//            return Mono.just("ip:" + (ip == null ? "unknown" : ip.getAddress().getHostAddress()));
//        };
//    }
//}
///* Nếu header có X-User-Id (được set bởi AuthenticationFilter) thì dùng userId đó làm key để đếm rate-limit.
//Nếu không có, fallback dùng địa chỉ IP client.*/