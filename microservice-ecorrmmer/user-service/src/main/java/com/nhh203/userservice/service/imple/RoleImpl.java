package com.nhh203.userservice.service.imple;

import com.nhh203.userservice.exception.wrapper.RoleNotFoundException;
import com.nhh203.userservice.exception.wrapper.UserNotFoundException;
import com.nhh203.userservice.model.entity.Role;
import com.nhh203.userservice.model.entity.RoleName;
import com.nhh203.userservice.model.entity.User;
import com.nhh203.userservice.repository.RoleRepository;
import com.nhh203.userservice.repository.UserRepository;
import com.nhh203.userservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RoleImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    @Override
    public Optional<Role> findByName(RoleName name) {
        return Optional.of(roleRepository.findByName(name).orElseThrow(() -> new RoleNotFoundException("Role Not Found with name: " + name)));
    }

    @Override
    public boolean assignRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));


        Role role = roleRepository.findByName(mapToRoleName(roleName))
                .orElseThrow(() -> new RoleNotFoundException("Role not found in system: " + roleName));

        if (user.getRoles().contains(role))
            return false;

        user.getRoles().add(role);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean revokeRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (user.getRoles().removeIf(role -> role.name().equals(mapToRoleName(roleName)))) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getUserRoles(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        List<String> roleNames = new ArrayList<>();
        user.getRoles().forEach(userRole -> roleNames.add(userRole.name().toString()));
        return roleNames;

    }


    private RoleName mapToRoleName(String roleName) {
        return switch (roleName) {
            case "ADMIN", "admin", "Admin" -> RoleName.ADMIN;
            case "USER", "user", "User" -> RoleName.USER;
            default -> null;
        };
    }

}
