// 📁 org/example/amlak/controller/RoleController.java
package org.example.core.controller;

import org.example.core.dto.RoleCreateRequest;
import org.example.core.dto.RolePermissionUpdateRequest; // DTO جدید را ایمپورت کنید
import org.example.core.model.Role;
import org.example.core.repository.RoleRepository;
import org.example.core.service.RoleService; // سرویس جدید را ایمپورت کنید
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // برای PathVariable
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping; // برای متد جدید حذف
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')") // تمام متدهای این کنترلر فقط برای کاربرانی با نقش ADMIN قابل دسترسی است
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService; // سرویس جدید را تزریق کنید

    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * دریافت جزئیات یک نقش بر اساس نام آن (شامل لیست مجوزها).
     * @param roleName نام نقش.
     * @return شیء Role یا 404.
     */
    @GetMapping("/{roleName}") // GET /api/roles/{roleName}
    public ResponseEntity<?> getRoleByName(@PathVariable String roleName) {
        try {
            Role role = roleService.getRoleByName(roleName);
            return ResponseEntity.status(HttpStatus.CREATED).body("منو با موفقیت ایجاد شد!"); // 201 Created

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در دریافت نقش رخ داد: " + e.getMessage());
        }
    }


    /**
     * ایجاد یک نقش جدید و تخصیص مجوزها به آن.
     * @param request شامل نام نقش و لیست نام مجوزها.
     * @return ResponseEntity با وضعیت CREATED یا BAD_REQUEST.
     */
    @PostMapping // POST /api/roles
    public ResponseEntity<?> createRole(@RequestBody RoleCreateRequest request) {
        try {
            Role newRole = roleService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("نقش '" + newRole.getName() + "' با موفقیت ایجاد شد.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در ایجاد نقش رخ داد: " + e.getMessage());
        }
    }

    /**
     * افزودن یک مجوز به یک نقش خاص.
     * @param roleName نام نقشی که مجوز به آن اضافه می‌شود.
     * @param request شامل نام مجوزی که باید اضافه شود.
     * @return وضعیت OK یا خطا.
     */
    @PostMapping("/{roleName}/permissions") // POST /api/roles/{roleName}/permissions
    public ResponseEntity<?> addPermissionToRole(@PathVariable String roleName,
                                                 @RequestBody RolePermissionUpdateRequest request) {
        try {
            // اطمینان حاصل کنید که roleName در PathVariable و request body یکی هستند (اختیاری)
            if (!roleName.equals(request.getRoleName())) {
                return ResponseEntity.badRequest().body("نام نقش در مسیر و بدنه درخواست مطابقت ندارد.");
            }
            roleService.addPermissionToRole(roleName, request.getPermissionName());
            return ResponseEntity.ok().body("مجوز '" + request.getPermissionName() + "' با موفقیت به نقش '" + roleName + "' اضافه شد.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در افزودن مجوز به نقش رخ داد: " + e.getMessage());
        }
    }

    /**
     * حذف یک مجوز از یک نقش خاص.
     * @param roleName نام نقشی که مجوز از آن حذف می‌شود.
     * @param permissionName نام مجوزی که باید حذف شود.
     * @return وضعیت OK یا خطا.
     */
    @DeleteMapping("/{roleName}/permissions/{permissionName}") // DELETE /api/roles/{roleName}/permissions/{permissionName}
    public ResponseEntity<?> removePermissionFromRole(@PathVariable String roleName,
                                                      @PathVariable String permissionName) {
        try {
            roleService.removePermissionFromRole(roleName, permissionName);
            return ResponseEntity.ok().body("مجوز '" + permissionName + "' با موفقیت از نقش '" + roleName + "' حذف شد.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در حذف مجوز از نقش رخ داد: " + e.getMessage());
        }
    }
}