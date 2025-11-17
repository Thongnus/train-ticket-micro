package org.example.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Validated // Kích hoạt tính năng validation của Spring validate trong danh sách tham số trong dto
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestParam String username, @RequestHeader("Authorization") String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
        log.info("Validating token for user: {}", userService.validateToken(username, token));
        return ResponseEntity.ok().body(userService.validateToken(username, token));
    }



}
