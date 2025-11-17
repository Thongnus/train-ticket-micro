package org.example.userservice.dto.request;

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
    private String userName;
    private String passWord;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Set<String> roles;
}
