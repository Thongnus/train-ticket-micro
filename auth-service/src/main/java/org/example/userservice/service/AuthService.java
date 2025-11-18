package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.request.LoginRequest;
import org.example.userservice.dto.response.JwtResponse;
import org.example.userservice.entity.Role;
import org.example.userservice.entity.User;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.utils.TokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public JwtResponse login(LoginRequest req) {

        User user = userRepository.findByUsername(req.getUsername());
        if (user == null) {
            throw new BadCredentialsException("Username không tồn tại");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Sai mật khẩu");
        }

        String roleList = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, 
                    user.getRoles().stream()
                        .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(r.getName()))
                        .collect(Collectors.toList()));

        String token = tokenProvider.createToken(authentication, req.isRememberMe(), roleList);

        user.setToken(token);
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        return new JwtResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                authentication.getAuthorities()
        );
    }
}
