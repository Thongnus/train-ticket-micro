package org.example.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
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
