package org.example.userservice.security;

import com.example.commonservice.config.AuthEntryPointJwt;
import com.example.commonservice.config.BaseSecurityConfig;
import com.example.commonservice.config.JwtAuthenticationInServiceFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class UserServiceSecurityConfig extends BaseSecurityConfig {

    public UserServiceSecurityConfig(JwtAuthenticationInServiceFilter jwtAuthFilter, AuthEntryPointJwt authEntryPointJwt) {
        super(jwtAuthFilter, authEntryPointJwt);
    }

    @Override
    protected void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "api/auth/**",
                        "/api/public/**",
                        "/swagger-ui.html"
                ).permitAll()

                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "USER")

                .anyRequest().authenticated()
        );
    }
}
