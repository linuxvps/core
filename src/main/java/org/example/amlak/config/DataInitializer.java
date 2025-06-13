package org.example.amlak.config;

import org.example.amlak.model.Menu;
import org.example.amlak.model.Permission;
import org.example.amlak.model.Role;
import org.example.amlak.repository.MenuRepository;
import org.example.amlak.repository.PermissionRepository;
import org.example.amlak.repository.RoleRepository;
import org.example.amlak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // 1. ایجاد مجوزها و نقش‌ها (فقط اگر از قبل وجود ندارند)
        if (permissionRepository.count() == 0 && roleRepository.count() == 0) {
            System.out.println("✅ ایجاد مجوزها و نقش‌های اولیه...");

            // ایجاد مجوزهای اصلی (Permissions)
            Permission permViewDashboard = new Permission();
            permViewDashboard.setName("VIEW_DASHBOARD");

            Permission permManageUsers = new Permission();
            permManageUsers.setName("MANAGE_USERS");

            Permission permViewReports = new Permission();
            permViewReports.setName("VIEW_REPORTS");

            Permission permUserMenuManagement = new Permission();
            permUserMenuManagement.setName("PERM_USER_MENU_MANAGEMENT");

            Permission permUserList = new Permission();
            permUserList.setName("PERM_USER_LIST");

            Permission permCreateUser = new Permission();
            permCreateUser.setName("PERM_CREATE_USER");

            Permission permCreateRole = new Permission();
            permCreateRole.setName("PERM_CREATE_RO");

            permissionRepository.saveAll(Arrays.asList(
                    permViewDashboard,
                    permManageUsers,
                    permViewReports,
                    permUserMenuManagement,
                    permUserList,
                    permCreateUser,
                    permCreateRole
            ));
            System.out.println("✅ مجوزهای اولیه اضافه شدند.");

            // ایجاد نقش‌ها (Roles) و تخصیص مجوزها به آن‌ها
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setPermissions(new HashSet<>(Arrays.asList(
                    permViewDashboard,
                    permManageUsers,
                    permViewReports,
                    permUserMenuManagement,
                    permUserList,
                    permCreateUser,
                    permCreateRole
            )));
            roleRepository.save(adminRole);
            System.out.println("✅ نقش 'ROLE_ADMIN' اضافه شد.");

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setPermissions(new HashSet<>(Arrays.asList(
                    permViewDashboard,
                    permViewReports
            )));
            roleRepository.save(userRole);
            System.out.println("✅ نقش 'ROLE_USER' اضافه شد.");
        }

        // 3. ایجاد کاربر ادمین پیش‌فرض (این متد حالا فقط کاربر را با استفاده از نقش‌های موجود ایجاد می‌کند)
        userService.createDefaultAdminIfNotExists();

        // 4. ایجاد منوهای اولیه (اگر وجود ندارند)
        if (menuRepository.count() == 0) {
            Optional<Permission> permUserMenuManagementOpt = permissionRepository.findByName("PERM_USER_MENU_MANAGEMENT");
            Optional<Permission> permUserListOpt = permissionRepository.findByName("PERM_USER_LIST");
            Optional<Permission> permCreateUserOpt = permissionRepository.findByName("PERM_CREATE_USER");
            Optional<Permission> permViewDashboardOpt = permissionRepository.findByName("VIEW_DASHBOARD");
            Optional<Permission> permCreateRoleOpt = permissionRepository.findByName("PERM_CREATE_RO");
            if (!permCreateRoleOpt.isPresent() || !permUserMenuManagementOpt.isPresent() || !permUserListOpt.isPresent() || !permCreateUserOpt.isPresent() || !permViewDashboardOpt.isPresent()) {
                System.err.println("❌ خطای دیتابیس: برخی از مجوزهای مورد نیاز برای منوها یافت نشدند. اطمینان حاصل کنید که این مجوزها در مرحله قبل ایجاد شده‌اند.");
                throw new IllegalStateException("Required permissions for menus not found during initialization.");
            }

            Permission permUserMenuManagement = permUserMenuManagementOpt.get();
            Permission permUserList = permUserListOpt.get();
            Permission permCreateUser = permCreateUserOpt.get();
            Permission permViewDashboard = permViewDashboardOpt.get();
            Permission permCreateRole = permCreateRoleOpt.get();

            Menu m1 = new Menu();
            m1.setTitle("دسترسی یوزر ها");
            m1.setUrl("/manage-user-menus.html");
            m1.setRequiredPermission(permUserMenuManagement);
            m1.setOrderIndex(1);

            Menu m2 = new Menu();
            m2.setTitle("لیست یوزر ها");
            m2.setUrl("/user-list.html");
            m2.setRequiredPermission(permUserList);
            m2.setOrderIndex(2);

            Menu m3 = new Menu();
            m3.setTitle("ایجاد یوزر");
            m3.setUrl("/create-user.html");
            m3.setRequiredPermission(permCreateUser);
            m3.setOrderIndex(3);

            Menu m4 = new Menu();
            m4.setTitle("ایجاد نقش");
            m4.setUrl("/create-role.html");
            m4.setRequiredPermission(permCreateRole);
            m4.setOrderIndex(4);

            Menu dashboardMenu = new Menu();
            dashboardMenu.setTitle("داشبورد");
            dashboardMenu.setUrl("/dashboard.html");
            dashboardMenu.setRequiredPermission(permViewDashboard);
            dashboardMenu.setOrderIndex(0);


            menuRepository.saveAll(Arrays.asList(m1, m2, m3, m4, dashboardMenu));
            System.out.println("✅ منوهای اولیه اضافه شدند.");
        }
    }
}