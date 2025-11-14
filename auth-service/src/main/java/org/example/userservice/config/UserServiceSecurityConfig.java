package org.example.userservice.config;

import com.example.commonservice.config.BaseSecurityConfig;
import com.example.commonservice.config.JwtAuthenticationInServiceFilter;
import com.example.commonservice.config.AuthEntryPointJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

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
                "/auth/**",
                "/api/public/**"
            ).permitAll()

            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "STAFF")

            .anyRequest().authenticated()
        );
    }
}
