package org.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.userservice.entity.Role;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    Long userId;
    String username;
    String password;
    String fullName;
    String email;
    String phone;
    String address;
    String idCard;
 //   @JsonFormat(pattern = "yyyy-MM-dd")
    Instant dateOfBirth;
  //  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  Instant createdAt;
//@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
Instant updatedAt;
    Set<Role> roles;
    Instant lastLogin;
    String status; // Thêm status
}