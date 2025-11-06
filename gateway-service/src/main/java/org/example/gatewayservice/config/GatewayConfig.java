//package org.example.gatewayservice.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
//import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
//
//    @Autowired
//    AuthenticationFilter filter;
//    @Bean
//    public RedisRateLimiter redisRateLimiter() {
//        // 60 req/s, burst 120, mỗi request 1 token
//        return new RedisRateLimiter(60, 120, 1);
//    }
//    @Bean
//    public RouteLocator routes(RouteLocatorBuilder builder,
//                               @Qualifier("customKeyResolver") KeyResolver keyResolver,
//                               RedisRateLimiter rateLimiter) {
//        return builder.routes()
//                .route("", r -> r.path("/limited/**")
//                        .filters(f -> f
//                                .filter((GatewayFilter) filter)
//                                .requestRateLimiter(config -> config
//                                        .setKeyResolver(keyResolver)
//                                        .setRateLimiter(rateLimiter))
//                        )
//                        .uri("http://example.org"))
//                .build();
//    }
//
//}
