package org.example.userservice.dto;

import lombok.Value;

import java.io.Serializable;

/**
 */
@Value
public class RoleDTO implements Serializable {
    int id;
    String name;
}