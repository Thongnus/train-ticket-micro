package org.example.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/users/hello")
    public String userFallback() {
        return "User service hello";
    }
    @GetMapping("/api/users")
    public String apiPublic() {
        return "public api fallback";
    }

    @GetMapping("/auth-fallback")
    public String authFallback() {
        return "Auth service is not available";
    }
}
