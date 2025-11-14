package org.example.userservice.service;

import com.example.betickettrain.dto.RoleDto;
import com.example.betickettrain.entity.Role;

import java.util.List;
import java.util.Optional;
public interface RoleService {
 Optional<Role> findByName(String name);


    List<RoleDto> getAllRoles();
}
