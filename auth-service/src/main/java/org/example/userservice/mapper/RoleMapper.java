package org.example.userservice.mapper;

import org.example.userservice.dto.RoleDTO;
import org.example.userservice.entity.Role;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    Role toEntity(RoleDTO roleDto);

    RoleDTO toDto(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Role partialUpdate(RoleDTO roleDto, @MappingTarget Role role);
}