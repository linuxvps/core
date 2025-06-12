package org.example.amlak.service;

import org.example.amlak.dto.CreateUserRequest;
import org.example.amlak.dto.UserResponse;
import org.example.amlak.model.Permission; // همچنان مورد نیاز برای DTOها و متد createUser
import org.example.amlak.model.Role;
import org.example.amlak.model.User;
import org.example.amlak.repository.PermissionRepository; // همچنان مورد نیاز برای DTOها و متد createUser
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
    private PermissionRepository permissionRepository; // این Repository همچنان برای مدیریت Permissions در createUser نیاز است
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * متدی برای ایجاد کاربر ادمین پیش‌فرض اگر دیتابیس خالی است.
     * فرض می‌شود که نقش‌ها (از جمله ROLE_ADMIN) قبلاً توسط DataInitializer ایجاد شده‌اند.
     */
    public void createDefaultAdminIfNotExists() {
        if (userRepository.count() == 0) {
            System.out.println("✅ ایجاد کاربر ادمین پیش‌فرض...");

            // 1. یافتن نقش ادمین (فرض می‌شود که این نقش قبلاً در DataInitializer ایجاد شده است)
            Optional<Role> adminRoleOpt = roleRepository.findByName("ROLE_ADMIN");
            if (!adminRoleOpt.isPresent()) {
                System.err.println("❌ خطای راه‌اندازی: نقش 'ROLE_ADMIN' یافت نشد. نمی‌توان ادمین پیش‌فرض را ایجاد کرد.");
                throw new IllegalStateException("ROLE_ADMIN not found during default admin creation. Ensure DataInitializer runs first.");
            }
            Role adminRole = adminRoleOpt.get();

            // 2. ایجاد کاربر ادمین پیش‌فرض
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // هش کردن رمز عبور
            admin.setEnabled(true);
            admin.setRoles(Collections.singleton(adminRole)); // تخصیص نقش ادمین

            userRepository.save(admin);

            System.out.println("✅ ادمین ساخته شد: admin / admin123");
        }
    }

    /**
     * ایجاد یک کاربر جدید بر اساس اطلاعات درخواست.
     *
     * @param request شیء CreateUserRequest شامل نام کاربری، رمز عبور و لیست نقش‌ها.
     */
    public void createUser(CreateUserRequest request) {
        // بررسی وجود کاربر
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("نام کاربری " + request.getUsername() + " قبلاً موجود است.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // هش کردن رمز عبور
        user.setEnabled(true);

        // یافتن نقش‌ها بر اساس نام‌های ارسالی در درخواست
        Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(request.getRoles()));
        if (roles.size() != request.getRoles().size()) {
            // اگر تعداد نقش‌های یافت شده با تعداد درخواست شده متفاوت است، یعنی یک یا چند نقش نامعتبر هستند.
            throw new IllegalArgumentException("یک یا چند نقش ارسال شده نامعتبر هستند.");
        }
        user.setRoles(roles);

        userRepository.save(user);
    }

    /**
     * دریافت لیست تمام کاربران به همراه نقش‌هایشان.
     *
     * @return لیستی از UserResponse ها.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName).collect(Collectors.toList());
            return new UserResponse(user.getId(), user.getUsername(), roleNames);
        }).collect(Collectors.toList());
    }

    /**
     * حذف یک کاربر بر اساس شناسه.
     *
     * @param id شناسه کاربر مورد نظر برای حذف.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("کاربری با شناسه " + id + " یافت نشد.");
        }
        userRepository.deleteById(id);
    }
}