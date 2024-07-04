package com.nhh203.userservice.service;

import com.nhh203.userservice.model.entity.Role;
import com.nhh203.userservice.model.entity.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Optional<Role> findByName(RoleName name);

    boolean assignRole(Long id, String roleName);

    boolean revokeRole(Long id, String roleName);

    List<String> getUserRoles(Long id);

}
