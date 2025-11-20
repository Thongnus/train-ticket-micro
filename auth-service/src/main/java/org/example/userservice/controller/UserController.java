package org.example.userservice.controller;

import com.example.commonservice.entity.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.entity.User;
import org.example.userservice.service.UserService;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "all") String role,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserDTO> users = userService.findUsers(searchTerm, role, status, pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO UserDTO) {
        UserDTO createdUser = userService.createUser(UserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO UserDTO) {
//        UserDTO updatedUser = userService.updateUser(id, UserDTO);
//        return ResponseEntity.ok(updatedUser);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserDTO>> getMyProfile(@AuthenticationPrincipal User user) {
        Integer id = user.getUserId();
        return ResponseEntity.ok(SuccessResponse.of(userService.findUserById(id)));
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserDTO UserDTO,
                                     @AuthenticationPrincipal User userDetails) {
        String username = userDetails.getUsername();
        return  ResponseEntity.ok(userService.update(UserDTO, username));
    }
    @PatchMapping("/status/{id}")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, @RequestBody String statusUpdate) {
        UserDTO updatedUser = userService.toggleStatus(id,statusUpdate );
        return ResponseEntity.ok(SuccessResponse.of(updatedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.findUserById(id);
        return ResponseEntity.ok(SuccessResponse.of(user));
    }

}
