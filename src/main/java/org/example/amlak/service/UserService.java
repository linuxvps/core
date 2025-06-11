package org.example.amlak.service;

import org.example.amlak.dto.CreateUserRequest;
import org.example.amlak.dto.UserResponse;
import org.example.amlak.model.Permission;
import org.example.amlak.model.Role;
import org.example.amlak.model.User;
import org.example.amlak.repository.PermissionRepository;
import org.example.amlak.repository.RoleRepository;
import org.example.amlak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createDefaultAdminIfNotExists() {
        if (userRepository.count() == 0) {
            Permission perm = new Permission();
            perm.setName("MANAGE_ALL");
            permissionRepository.save(perm);

            Role role = new Role();
            role.setName("ROLE_ADMIN");
            role.setPermissions(Collections.singleton(perm));
            roleRepository.save(role);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Collections.singleton(role));
            admin.setEnabled(true);

            userRepository.save(admin);

            System.out.println("✅ ادمین ساخته شد: admin / admin123");
        }
    }

    public void createUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(request.getRoles()));
        user.setRoles(roles);

        userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName).collect(Collectors.toList());
            return new UserResponse(user.getId(), user.getUsername(), roleNames);
        }).collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
