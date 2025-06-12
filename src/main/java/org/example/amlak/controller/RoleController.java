package org.example.amlak.controller;

import org.example.amlak.model.Role;
import org.example.amlak.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // این ایمپورت ممکن است در برخی موارد نیاز نباشد اما برای کد استاندارد مفید است
import org.springframework.security.access.prepost.PreAuthorize; // برای محافظت از اندپوینت
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // نشان می‌دهد که این یک کنترلر REST است
@RequestMapping("/api/roles") // مسیر پایه برای تمام اندپوینت‌های این کنترلر
@PreAuthorize("hasRole('ADMIN')") // تمام متدهای این کنترلر فقط برای کاربرانی با نقش ADMIN قابل دسترسی است
public class RoleController {

    @Autowired // تزریق وابستگی RoleRepository
    private RoleRepository roleRepository;

    /**
     * دریافت لیست تمام نقش‌ها.
     * این اندپوینت برای فرانت‌اند (مثلاً در صفحه مدیریت نقش‌های کاربران) استفاده می‌شود.
     * @return لیستی از تمام اشیاء Role.
     */
    @GetMapping // GET /api/roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll(); // دریافت تمام نقش‌ها از دیتابیس
    }
}