package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserDto;
import org.example.userservice.dto.request.SignUpRequest;
import org.example.userservice.entity.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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
    public UserDto registerUser(SignUpRequest signupRequest) throws RoleNotFoundException {
        return null;
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
    public User update(UserDto user, String username) {
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
    public Page<UserDto> findUsers(String search, String role, String status, Pageable pageable) {
        return null;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public UserDto toggleStatus(Long id, String status) {
        return null;
    }

    @Override
    public UserDto findUserById(Long id) {
        return null;
    }
}
