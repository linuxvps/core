package org.example.amlak.service;

import org.example.amlak.model.Permission;
import org.example.amlak.model.Role;
import org.example.amlak.model.User;
import org.example.amlak.repository.PermissionRepository;
import org.example.amlak.repository.RoleRepository;
import org.example.amlak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
}
