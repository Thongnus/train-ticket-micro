package org.example.userservice.service;

import com.example.commonservice.exceptionHandle.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.SignUpRequest;
import org.example.userservice.entity.Role;
import org.example.userservice.entity.User;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.RoleRepository;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    @Override
    public User findbyUsername(String name) {
        return null;
    }

    @Override
    public boolean validateToken(String username, String token) {
        User user = userRepository.findByUsernameAndToken(username, token);
        if (user == null || !Objects.equals(user.getStatus(), User.Status.active.toString()) || !token.equals(user.getToken())) {
            throw new BadCredentialsException("Invalid username/token");
        }
        return true;
    }

    @Override
    public User saveTT(User user) {
        return null;
    }

    @Override
    public UserDTO registerUser(SignUpRequest signupRequest) throws RoleNotFoundException {
        if (userRepository.existsUserByUsername(signupRequest.getUserName())) {
            throw new RuntimeException("Username is already taken!");
        }

        User user = createUserFromRequest(signupRequest);
        return userMapper.toDto(userRepository.save(user));

    }
    private User createUserFromRequest(SignUpRequest req) throws RoleNotFoundException {
        return User.builder()
                .username(req.getUserName())
                .password(passwordEncoder.encode(req.getPassWord()))
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhoneNumber())
                .roles(assignRoles(req.getRoles()))
                .status(User.Status.active.toString())
                .lastLogin(null)
                .createdAt(Instant.now())
                .build();

    }
    private Set<Role> assignRoles(Set<String> requestedRoles) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        // Default role
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(Constants.Role.ROLE_USER);
            if (userRole == null) throw new NotFoundException("Role CUSTOMER not found");
            roles.add(userRole);
        }
        // Admin role if requested
        if (requestedRoles != null && requestedRoles.contains("ADMIN")) {
            Role adminRole = roleRepository.findByName(Constants.Role.ROLE_ADMIN);
            if (adminRole == null) throw new NotFoundException("Role ADMIN not found");
            roles.add(adminRole);
        }

        return roles;
    }
    @Override
    public Page<User> getallUser(Pageable pageable) {
        return null;
    }

    @Override
    public User updatepassword(User u) {
        return null;
    }

    @Override
    public User update(UserDTO user, String username) {
        return null;
    }

    @Override
    public User updatenopassword(User user) {
        return null;
    }

    @Override
    public User findbyId(Long id) {
        return null;
    }

    @Override
    public void deleteuserbyID(int id) {

    }

    @Override
    public ArrayList<User> searchbyName(String name) {
        return null;
    }

    @Override
    public Page<UserDTO> findUsers(String search, String role, String status, Pageable pageable) {
        return null;
    }

    @Override
    public UserDTO createUser(UserDTO userDto) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public UserDTO toggleStatus(Long id, String status) {
        return null;
    }

    @Override
    public UserDTO findUserById(Integer id) {
        return null;
    }
}
