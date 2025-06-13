// 📁 org/example/amlak/controller/PermissionController.java
package org.example.amlak.controller;

import org.example.amlak.dto.PermissionCreateRequest; // DTO جدید را ایمپورت کنید
import org.example.amlak.model.Permission;
import org.example.amlak.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // برای متد جدید
import org.springframework.web.bind.annotation.RequestBody; // برای دریافت DTO در POST
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("hasRole('ADMIN')") // تمام متدهای این کنترلر فقط برای کاربرانی با نقش ADMIN قابل دسترسی است
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

    /**
     * ایجاد یک مجوز جدید.
     * @param request شامل نام مجوز.
     * @return ResponseEntity با وضعیت CREATED یا BAD_REQUEST.
     */
    @PostMapping // POST /api/permissions
    public ResponseEntity<?> createPermission(@RequestBody PermissionCreateRequest request) {
        // بررسی کنید که نام مجوز خالی نباشد
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("نام مجوز نمی‌تواند خالی باشد.");
        }
        // بررسی کنید که مجوز با همین نام از قبل وجود نداشته باشد
        if (permissionRepository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("مجوز با نام '" + request.getName() + "' قبلاً موجود است."); // 409 Conflict
        }
        try {
            Permission newPermission = new Permission();
            newPermission.setName(request.getName());
            permissionRepository.save(newPermission);
            return ResponseEntity.status(HttpStatus.CREATED).body("مجوز '" + newPermission.getName() + "' با موفقیت ایجاد شد.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در ایجاد مجوز رخ داد: " + e.getMessage());
        }
    }

    // TODO: می‌توانید متدهای حذف، به‌روزرسانی یا لیست مجوزها را اینجا اضافه کنید.
}