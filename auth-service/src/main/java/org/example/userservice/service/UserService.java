package org.example.userservice.service;


import org.example.userservice.dto.UserDTO;
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
    UserDTO registerUser(SignUpRequest signupRequest) throws RoleNotFoundException;

    Page<User> getallUser(Pageable pageable);

    User updatepassword(User u);

    User update(UserDTO user, String username);

    User updatenopassword(User user);

    User findbyId(Long id);

    void deleteuserbyID(int id);

    ArrayList<User> searchbyName(String name);

    Page<UserDTO> findUsers(String search, String role, String status, Pageable pageable);
    UserDTO createUser(UserDTO userDto);
//    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDTO toggleStatus(Long id, String status);
    UserDTO findUserById(Integer id);
}
