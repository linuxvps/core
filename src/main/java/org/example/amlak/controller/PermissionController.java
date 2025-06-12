// 📁 org/example/amlak/controller/PermissionController.java (جدید)
package org.example.amlak.controller;

import org.example.amlak.model.Permission;
import org.example.amlak.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("hasRole('ADMIN')") // فقط ادمین‌ها باید بتوانند لیست مجوزها را ببینند
public class PermissionController {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * دریافت لیست تمام مجوزها.
     * این اندپوینت برای فرانت‌اند (مثلاً در صفحه ایجاد نقش) استفاده می‌شود.
     * @return لیستی از تمام اشیاء Permission.
     */
    @GetMapping // GET /api/permissions
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
}