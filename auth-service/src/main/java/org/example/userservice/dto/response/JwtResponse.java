package org.example.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Integer id;
    private String username;
    private Collection<? extends GrantedAuthority> roles;
}