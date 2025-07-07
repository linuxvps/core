package org.example.core.config;

import org.example.core.dto.CreateUserRequest;
import org.example.core.model.Permission;
import org.example.core.model.Role;
import org.example.core.model.enums.UserType;
import org.example.core.repository.MenuRepository;
import org.example.core.repository.PermissionRepository;
import org.example.core.repository.RoleRepository;
import org.example.core.repository.UserRepository;
import org.example.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public DataInitializer(UserService userService, MenuRepository menuRepository, PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.userService = userService;
        this.menuRepository = menuRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // 1. ایجاد نقش‌ها و مجوزها اگر وجود ندارند
        if (roleRepository.count() == 0) {
            createPermissionsAndRoles();
        }

        // 2. ایجاد کاربر ادمین پیش‌فرض اگر هیچ کاربری وجود ندارد
        userService.createDefaultAdminIfNotExists();

        // 3. ایجاد منوهای اولیه اگر وجود ندارند
        if (menuRepository.count() == 0) {
            createMenus();
        }

        // 4. ایجاد کاربران نمونه برای تست
        createSampleUsersIfNotFound();
    }

    private void createPermissionsAndRoles() {
        System.out.println("✅ Creating initial permissions and roles...");

        // تعریف و ذخیره مجوزها
        Permission permViewDashboard = createAndSavePermission("VIEW_DASHBOARD");
        Permission permManageUsers = createAndSavePermission("MANAGE_USERS");
        Permission permViewReports = createAndSavePermission("VIEW_REPORTS");

        // تعریف و ذخیره نقش‌ها با مجوزهای مربوطه
        createAndSaveRole("ROLE_ADMIN", permViewDashboard, permManageUsers, permViewReports);
        createAndSaveRole("ROLE_PROFESSIONAL", permViewDashboard);
        createAndSaveRole("ROLE_CLIENT", permViewDashboard);

        System.out.println("✅ Initial permissions and roles created.");
    }

    // متد کمکی برای ایجاد و ذخیره یک مجوز
    private Permission createAndSavePermission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permissionRepository.save(permission);
    }

    // متد کمکی برای ایجاد و ذخیره یک نقش
    private void createAndSaveRole(String name, Permission... permissions) {
        Role role = new Role();
        role.setName(name);
        role.setPermissions(new HashSet<>(List.of(permissions)));
        roleRepository.save(role);
    }

    private void createMenus() {
        // منطق ایجاد منوهای شما در اینجا قرار می‌گیرد
        System.out.println("✅ Creating initial menu items...");
        // ... (your menu creation logic)
    }

    private void createSampleUsersIfNotFound() {
        if (userRepository.count() < 5) {
            System.out.println("✅ Creating sample users...");

            List<CreateUserRequest> sampleUsers = List.of(
                    buildUserRequest("سارا", "رضایی", "sara.rezaei@example.com", "09121112233", UserType.PROFESSIONAL),
                    buildUserRequest("علی", "احمدی", "ali.ahmadi@example.com", "09124445566", UserType.PROFESSIONAL),
                    buildUserRequest("مریم", "حسینی", "maryam.hosseini@example.com", "09127778899", UserType.CLIENT)
            );

            sampleUsers.forEach(request -> {
                if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
                    try {
                        // این متد باید در UserService اصلاح شود
                        userService.createUser(request);
                        System.out.println(" -> Created " + request.getUserType() + ": " + request.getUsername());
                    } catch (Exception e) {
                        System.err.println("❌ Error creating sample user '" + request.getUsername() + "': " + e.getMessage());
                        e.printStackTrace(); // چاپ کامل خطا برای دیباگ بهتر
                    }
                }
            });
        }
    }

    private CreateUserRequest buildUserRequest(String firstName, String lastName, String email, String phone, UserType userType) {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setUsername(email);
        request.setPassword("password123");
        request.setPhoneNumber(phone);
        request.setUserType(userType);

        if (UserType.PROFESSIONAL.equals(userType)) {
            request.setRoles(List.of("ROLE_PROFESSIONAL"));
        } else {
            request.setRoles(List.of("ROLE_CLIENT"));
        }

        return request;
    }
}
