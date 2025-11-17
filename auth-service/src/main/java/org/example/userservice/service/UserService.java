package org.example.userservice.service;


import org.example.userservice.dto.UserDto;
import org.example.userservice.dto.request.SignUpRequest;
import org.example.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;

public interface UserService {
    User findbyUsername(String name);
     boolean validateToken(String username, String token);
    User saveTT(User user);

    // UserService method
    UserDto registerUser(SignUpRequest signupRequest) throws RoleNotFoundException;

    Page<User> getallUser(Pageable pageable);

    User updatepassword(User u);

    User update(UserDto user, String username);

    User updatenopassword(User user);

    User findbyId(Long id);

    void deleteuserbyID(int id);

    ArrayList<User> searchbyName(String name);

    Page<UserDto> findUsers(String search, String role, String status, Pageable pageable);
    UserDto createUser(UserDto userDto);
//    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto toggleStatus(Long id, String status);
    UserDto findUserById(Long id);
}
