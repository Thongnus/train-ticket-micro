package org.example.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
     @NotBlank(message = "Username không được để trống")
     String userName;
     @NotBlank(message = "Password không được để trống")
     String passWord;
     String fullName;
     @NotBlank(message = "Email không được để trống")
     @Email(message = "Email không hợp lệ")
     String email;
     String phoneNumber;
     Set<String> roles;
}
