package com.nhh203.userservice.service;

import com.nhh203.userservice.model.entity.Role;

public interface RoleService {

    Role findRoleByName(String name);

    Role addRole(String roleName);
}
