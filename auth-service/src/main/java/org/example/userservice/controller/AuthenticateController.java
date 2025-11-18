package org.example.userservice.controller;

import com.example.commonservice.entity.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.LoginRequest;
import org.example.userservice.dto.request.SignUpRequest;
import org.example.userservice.dto.response.JwtResponse;
import org.example.userservice.service.AuthService;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticateController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<?>> login(@RequestBody LoginRequest req) {
        JwtResponse jwtResponse = authService.login(req);
        return  ResponseEntity.ok(SuccessResponse.of(jwtResponse));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        log.info("Register attempt for user: {}", signUpRequest.getUserName());

        try {
            UserDTO createdUser = userService.registerUser(signUpRequest);
            log.info("User {} registered successfully", signUpRequest.getUserName());

            return ResponseEntity.ok("User registered successfully!");

        } catch (RoleNotFoundException e) {
            log.warn("Registration failed - username already exists: {}", signUpRequest.getUserName());
            return ResponseEntity.badRequest().body((e.getMessage()));

        }
    }

}
