// 📁 org/example/amlak/service/RoleService.java
package org.example.amlak.service;

import org.example.amlak.dto.RoleCreateRequest;
import org.example.amlak.dto.RolePermissionUpdateRequest; // DTO جدید را ایمپورت کنید
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
    private PermissionRepository permissionRepository;

    /**
     * ایجاد یک نقش جدید و تخصیص مجوزهای مشخص شده به آن.
     * @param request شامل نام نقش و لیست نام مجوزها.
     * @return Role ایجاد شده.
     * @throws IllegalArgumentException اگر نقشی با همین نام از قبل وجود داشته باشد یا مجوزی یافت نشود.
     */
    public Role createRole(RoleCreateRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("نقش با نام '" + request.getName() + "' قبلاً موجود است.");
        }

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissionNames() != null && !request.getPermissionNames().isEmpty()) {
            permissions = new HashSet<>(permissionRepository.findByNameIn(request.getPermissionNames()));

            Set<String> foundPermissionNames = permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            List<String> notFoundPermissions = request.getPermissionNames().stream()
                    .filter(name -> !foundPermissionNames.contains(name))
                    .collect(Collectors.toList());
            if (!notFoundPermissions.isEmpty()) {
                throw new IllegalArgumentException("یک یا چند مجوز یافت نشدند: " + String.join(", ", notFoundPermissions));
            }
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }

    /**
     * دریافت لیست تمام نقش‌ها.
     * @return لیستی از تمام اشیاء Role.
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * دریافت یک نقش بر اساس نام.
     * @param roleName نام نقش.
     * @return شیء Role.
     * @throws NoSuchElementException اگر نقش یافت نشود.
     */
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NoSuchElementException("نقش با نام '" + roleName + "' یافت نشد."));
    }

    /**
     * افزودن یک مجوز به یک نقش.
     * @param roleName نام نقش.
     * @param permissionName نام مجوزی که باید اضافه شود.
     * @throws NoSuchElementException اگر نقش یا مجوز یافت نشود.
     * @throws IllegalArgumentException اگر مجوز از قبل به نقش اختصاص یافته باشد.
     */
    public Role addPermissionToRole(String roleName, String permissionName) {
        Role role = getRoleByName(roleName);
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new NoSuchElementException("مجوز با نام '" + permissionName + "' یافت نشد."));

        if (role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException("مجوز '" + permissionName + "' قبلاً به نقش '" + roleName + "' اختصاص یافته است.");
        }

        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    /**
     * حذف یک مجوز از یک نقش.
     * @param roleName نام نقش.
     * @param permissionName نام مجوزی که باید حذف شود.
     * @throws NoSuchElementException اگر نقش یا مجوز یافت نشود.
     * @throws IllegalArgumentException اگر مجوز به نقش اختصاص نیافته باشد.
     */
    public Role removePermissionFromRole(String roleName, String permissionName) {
        Role role = getRoleByName(roleName);
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new NoSuchElementException("مجوز با نام '" + permissionName + "' یافت نشد."));

        if (!role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException("مجوز '" + permissionName + "' به نقش '" + roleName + "' اختصاص نیافته است.");
        }

        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }
}