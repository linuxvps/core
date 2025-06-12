// 📁 org/example/amlak/controller/RoleController.java
package org.example.amlak.controller;

import org.example.amlak.dto.RoleCreateRequest; // DTO جدید را ایمپورت کنید
import org.example.amlak.model.Role;
import org.example.amlak.repository.RoleRepository;
import org.example.amlak.service.RoleService; // سرویس جدید یا متد جدید در RoleService نیاز است
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
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')") // تمام متدهای این کنترلر فقط برای کاربرانی با نقش ADMIN قابل دسترسی است
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService; // نیاز به RoleService (یا می‌توانست در UserService باشد)

    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * ایجاد یک نقش جدید و تخصیص مجوزها به آن.
     * @param request شامل نام نقش و لیست نام مجوزها.
     * @return ResponseEntity با وضعیت CREATED یا BAD_REQUEST.
     */
    @PostMapping // POST /api/roles
    public ResponseEntity<?> createRole(@RequestBody RoleCreateRequest request) {
        try {
            Role newRole = roleService.createRole(request); // متد جدید در RoleService
            return ResponseEntity.status(HttpStatus.CREATED).body("نقش '" + newRole.getName() + "' با موفقیت ایجاد شد.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در ایجاد نقش رخ داد: " + e.getMessage());
        }
    }

    // TODO: می‌توانید متدهای حذف نقش، به‌روزرسانی نقش را اینجا اضافه کنید.
}