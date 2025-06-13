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
        // 1. ایجاد مجوزها و نقش‌ها (فقط اگر دیتابیس Permissions یا Roles خالی باشد)
        if (permissionRepository.count() == 0 && roleRepository.count() == 0) {
            System.out.println("✅ ایجاد مجوزها و نقش‌های اولیه...");

            // تعریف تمام مجوزها (Permissions)
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
            permCreateRole.setName("PERM_CREATE_ROLE"); // CORRECTED: Typo fixed here

            Permission permCreatePermission = new Permission(); // NEW: Permission for creating permissions
            permCreatePermission.setName("PERM_CREATE_PERMISSION");

            Permission permManageRolePermissions = new Permission(); // NEW: Permission for managing role permissions
            permManageRolePermissions.setName("PERM_MANAGE_ROLE_PERMISSIONS");


            // ذخیره تمام مجوزها در دیتابیس
            permissionRepository.saveAll(Arrays.asList(
                    permViewDashboard,
                    permManageUsers,
                    permViewReports,
                    permUserMenuManagement,
                    permUserList,
                    permCreateUser,
                    permCreateRole,
                    permCreatePermission,        // NEW: Add to saveAll list
                    permManageRolePermissions    // NEW: Add to saveAll list
            ));
            System.out.println("✅ مجوزهای اولیه اضافه شدند.");

            // تعریف نقش‌ها (Roles) و تخصیص مجوزها به آن‌ها
            // نقش ادمین با تمام مجوزها
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setPermissions(new HashSet<>(Arrays.asList(
                    permViewDashboard,
                    permManageUsers,
                    permViewReports,
                    permUserMenuManagement,
                    permUserList,
                    permCreateUser,
                    permCreateRole,
                    permCreatePermission,        // NEW: Add to admin role permissions
                    permManageRolePermissions    // NEW: Add to admin role permissions
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

        // 2. ایجاد کاربر ادمین پیش‌فرض (این متد فقط کاربر را با استفاده از نقش‌های موجود ایجاد می‌کند)
        // این متد فقط در صورتی کاربر ادمین را ایجاد می‌کند که هیچ کاربری در سیستم وجود نداشته باشد.
        userService.createDefaultAdminIfNotExists();

        // 3. ایجاد منوهای اولیه (فقط اگر از قبل وجود ندارند)
        if (menuRepository.count() == 0) {
            System.out.println("✅ ایجاد منوهای اولیه...");

            // پیدا کردن اشیاء Permission که قبلاً ایجاد و ذخیره شده‌اند.
            // استفاده از Optional.get() پس از isPresent() ایمن است زیرا شرط if تضمین می‌کند که آن‌ها قبلاً ذخیره شده‌اند.
            Optional<Permission> permUserMenuManagementOpt = permissionRepository.findByName("PERM_USER_MENU_MANAGEMENT");
            Optional<Permission> permUserListOpt = permissionRepository.findByName("PERM_USER_LIST");
            Optional<Permission> permCreateUserOpt = permissionRepository.findByName("PERM_CREATE_USER");
            Optional<Permission> permViewDashboardOpt = permissionRepository.findByName("VIEW_DASHBOARD");
            Optional<Permission> permCreateRoleOpt = permissionRepository.findByName("PERM_CREATE_ROLE"); // CORRECTED: Use correct name here
            Optional<Permission> permCreatePermissionOpt = permissionRepository.findByName("PERM_CREATE_PERMISSION"); // NEW: Get new permission
            Optional<Permission> permManageRolePermissionsOpt = permissionRepository.findByName("PERM_MANAGE_ROLE_PERMISSIONS"); // NEW: Get new permission

            // بررسی نهایی برای اطمینان از وجود همه مجوزها (اگرچه با پاکسازی دیتابیس، این نباید مشکل باشد)
            if (!permUserMenuManagementOpt.isPresent() ||
                    !permUserListOpt.isPresent() ||
                    !permCreateUserOpt.isPresent() ||
                    !permViewDashboardOpt.isPresent() ||
                    !permCreateRoleOpt.isPresent() ||
                    !permCreatePermissionOpt.isPresent() ||      // NEW: Check for new permission
                    !permManageRolePermissionsOpt.isPresent()    // NEW: Check for new permission
            ) {
                System.err.println("❌ خطای دیتابیس: برخی از مجوزهای مورد نیاز برای منوها یافت نشدند. اطمینان حاصل کنید که این مجوزها در مرحله قبل ایجاد شده‌اند.");
                throw new IllegalStateException("Required permissions for menus not found during initialization.");
            }

            // استخراج اشیاء Permission از Optional ها
            Permission permUserMenuManagement = permUserMenuManagementOpt.get();
            Permission permUserList = permUserListOpt.get();
            Permission permCreateUser = permCreateUserOpt.get();
            Permission permCreateRole = permCreateRoleOpt.get();
            Permission permViewDashboard = permViewDashboardOpt.get();
            Permission permCreatePermission = permCreatePermissionOpt.get();      // NEW: Get it
            Permission permManageRolePermissions = permManageRolePermissionsOpt.get(); // NEW: Get it


            // تعریف و اختصاص منوها به مجوزهای مربوطه
            Menu m1 = new Menu();
            m1.setTitle("مدیریت نقش و منوهای کاربران"); // نام واضح‌تر برای منو
            m1.setUrl("/manage-user-menus.html");
            m1.setRequiredPermission(permUserMenuManagement);
            m1.setOrderIndex(1);

            Menu m2 = new Menu();
            m2.setTitle("لیست کاربران");
            m2.setUrl("/user-list.html");
            m2.setRequiredPermission(permUserList);
            m2.setOrderIndex(2);

            Menu m3 = new Menu();
            m3.setTitle("ایجاد کاربر");
            m3.setUrl("/create-user.html");
            m3.setRequiredPermission(permCreateUser);
            m3.setOrderIndex(3);

            Menu m4 = new Menu();
            m4.setTitle("ایجاد نقش");
            m4.setUrl("/create-role.html");
            m4.setRequiredPermission(permCreateRole);
            m4.setOrderIndex(4);

            Menu m5 = new Menu(); // NEW: Menu for creating permissions
            m5.setTitle("ایجاد مجوز");
            m5.setUrl("/create-permission.html");
            m5.setRequiredPermission(permCreatePermission);
            m5.setOrderIndex(5);

            Menu m6 = new Menu(); // NEW: Menu for managing role permissions
            m6.setTitle("مدیریت مجوزهای نقش‌ها");
            m6.setUrl("/manage-role-permissions.html");
            m6.setRequiredPermission(permManageRolePermissions);
            m6.setOrderIndex(6);

            Menu dashboardMenu = new Menu();
            dashboardMenu.setTitle("داشبورد");
            dashboardMenu.setUrl("/dashboard.html");
            dashboardMenu.setRequiredPermission(permViewDashboard);
            dashboardMenu.setOrderIndex(0); // مرتبه نمایش بالاتر برای داشبورد

            // ذخیره تمام منوها در دیتابیس
            menuRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5, m6, dashboardMenu)); // NEW: All menus added
            System.out.println("✅ منوهای اولیه اضافه شدند.");
        }
    }
}