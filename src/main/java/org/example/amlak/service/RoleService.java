// 📁 org/example/amlak/service/RoleService.java
package org.example.amlak.service;

import org.example.amlak.dto.RoleCreateRequest;
import org.example.amlak.model.Permission;
import org.example.amlak.model.Role;
import org.example.amlak.repository.PermissionRepository;
import org.example.amlak.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository; // برای پیدا کردن مجوزها

    /**
     * ایجاد یک نقش جدید و تخصیص مجوزهای مشخص شده به آن.
     * @param request شامل نام نقش و لیست نام مجوزها.
     * @return Role ایجاد شده.
     * @throws IllegalArgumentException اگر نقشی با همین نام از قبل وجود داشته باشد یا مجوزی یافت نشود.
     */
    public Role createRole(RoleCreateRequest request) {
        // 1. بررسی وجود نقش با همین نام
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("نقش با نام '" + request.getName() + "' قبلاً موجود است.");
        }

        // 2. پیدا کردن اشیاء Permission بر اساس نام‌ها
        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissionNames() != null && !request.getPermissionNames().isEmpty()) {
            permissions = new HashSet<>(permissionRepository.findByNameIn(request.getPermissionNames()));

            // بررسی کنید که آیا تمام مجوزهای درخواستی پیدا شده‌اند
            if (permissions.size() != request.getPermissionNames().size()) {
                // می‌توانید دقیقاً بگویید کدام مجوزها پیدا نشده‌اند
                Set<String> foundPermissionNames = permissions.stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());
                List<String> notFoundPermissions = request.getPermissionNames().stream()
                        .filter(name -> !foundPermissionNames.contains(name))
                        .collect(Collectors.toList());
                throw new IllegalArgumentException("یک یا چند مجوز یافت نشدند: " + String.join(", ", notFoundPermissions));
            }
        }

        // 3. ایجاد و ذخیره نقش
        Role role = new Role();
        role.setName(request.getName());
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }

    // TODO: می‌توانید متدهای حذف نقش، به‌روزرسانی مجوزهای یک نقش را اینجا اضافه کنید.
    // مثال:
    // public void deleteRole(Long id) { ... }
    // public Role updateRolePermissions(Long roleId, List<String> newPermissionNames) { ... }
}