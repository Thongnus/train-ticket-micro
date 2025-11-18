package org.example.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated // Kích hoạt tính năng validation của Spring validate trong danh sách tham số trong dto
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }



}
