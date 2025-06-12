package org.example.amlak.config;

import org.example.amlak.model.Menu;
import org.example.amlak.model.Permission;
import org.example.amlak.model.Role; // باید کلاس Role را ایمپورت کنید
import org.example.amlak.repository.MenuRepository;
import org.example.amlak.repository.PermissionRepository;
import org.example.amlak.repository.RoleRepository; // باید RoleRepository را ایمپورت کنید
import org.example.amlak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserService userService;
    @Autowired private MenuRepository menuRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private RoleRepository roleRepository; // اضافه کردن RoleRepository

    @Override
    public void run(String... args) {
        // اطمینان از اینکه مجوزها و نقش‌ها فقط یک بار ایجاد می‌شوند
        if (permissionRepository.count() == 0 && roleRepository.count() == 0) {
            System.out.println("✅ ایجاد مجوزها و نقش‌های اولیه...");

            // 1. ایجاد مجوزهای اصلی (Permissions)
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

            permissionRepository.saveAll(Arrays.asList(
                    permViewDashboard,
                    permManageUsers,
                    permViewReports,
                    permUserMenuManagement,
                    permUserList,
                    permCreateUser
            ));
            System.out.println("✅ مجوزهای اولیه اضافه شدند.");

            // 2. ایجاد نقش‌ها (Roles) و تخصیص مجوزها به آن‌ها
            // نقش ادمین با تمام مجوزها
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setPermissions(new HashSet<>(Arrays.asList(
                    permViewDashboard,
                    permManageUsers,
                    permViewReports,
                    permUserMenuManagement,
                    permUserList,
                    permCreateUser
            )));
            roleRepository.save(adminRole);
            System.out.println("✅ نقش 'ROLE_ADMIN' اضافه شد.");


            // نقش کاربر عادی (مثال: فقط دسترسی به داشبورد و گزارشات)
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
            // پیدا کردن شیء Permission برای "PERM_USER_MENU_MANAGEMENT"
            Optional<Permission> permUserMenuManagementOpt = permissionRepository.findByName("PERM_USER_MENU_MANAGEMENT");
            // پیدا کردن شیء Permission برای "PERM_USER_LIST"
            Optional<Permission> permUserListOpt = permissionRepository.findByName("PERM_USER_LIST");
            // پیدا کردن شیء Permission برای "PERM_CREATE_USER"
            Optional<Permission> permCreateUserOpt = permissionRepository.findByName("PERM_CREATE_USER");
            // پیدا کردن شیء Permission برای "VIEW_DASHBOARD"
            Optional<Permission> permViewDashboardOpt = permissionRepository.findByName("VIEW_DASHBOARD");


            if (!permUserMenuManagementOpt.isPresent() || !permUserListOpt.isPresent() || !permCreateUserOpt.isPresent() || !permViewDashboardOpt.isPresent()) {
                System.err.println("❌ خطای دیتابیس: برخی از مجوزهای مورد نیاز برای منوها یافت نشدند. اطمینان حاصل کنید که این مجوزها در مرحله قبل ایجاد شده‌اند.");
                throw new IllegalStateException("Required permissions for menus not found during initialization.");
            }

            Permission permUserMenuManagement = permUserMenuManagementOpt.get();
            Permission permUserList = permUserListOpt.get();
            Permission permCreateUser = permCreateUserOpt.get();
            Permission permViewDashboard = permViewDashboardOpt.get();


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

            Menu dashboardMenu = new Menu();
            dashboardMenu.setTitle("داشبورد");
            dashboardMenu.setUrl("/dashboard.html");
            dashboardMenu.setRequiredPermission(permViewDashboard);
            dashboardMenu.setOrderIndex(0); // مرتبه نمایش بالاتر برای داشبورد

            menuRepository.saveAll(Arrays.asList(m1, m2, m3, dashboardMenu));
            System.out.println("✅ منوهای اولیه اضافه شدند.");
        }
    }
}