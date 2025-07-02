// 📁 org/example/amlak/controller/AdminMenuWebController.java
package org.example.core.controller;

import org.example.core.dto.MenuCreateRequest;
import org.example.core.model.Permission;
import org.example.core.repository.PermissionRepository;
import org.example.core.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // برای برگرداندن پاسخ JSON
import org.springframework.security.access.prepost.PreAuthorize; // برای امنیت
import org.springframework.web.bind.annotation.*; // RestController@ و RequestBody@

import java.util.List;

@RestController // این کنترلر API (JSON) را سرو می‌کند.
@RequestMapping("/api/admin/menus") // مسیر API برای مدیریت منوها (مسیر را به /api/admin/menus تغییر دادم تا با سایر API ها سازگار باشد)
@PreAuthorize("hasRole('ADMIN')") // فقط ادمین‌ها به این API دسترسی دارند
public class AdminMenuWebController { // نام کلاس می‌تواند AdminMenuRestController هم باشد

    @Autowired
    private MenuService menuService;
    @Autowired
    private PermissionRepository permissionRepository;

    // متد GET برای نمایش فرم حذف می‌شود، چون HTML استاتیک است.
    // اما ما به یک API برای گرفتن لیست مجوزها نیاز داریم.
    // از آنجایی که شما PermissionController.java را هم دارید، می‌توانیم از آن استفاده کنیم.
    // اگر نه، این متد را می‌توان اینجا اضافه کرد:

    @GetMapping("/permissions") // GET /api/admin/menus/permissions
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    // اما ترجیح این است که از PermissionController موجود استفاده کنیم.

    /**
     * پردازش ارسال فرم ایجاد منوی جدید.
     * @param request شیء MenuCreateRequest پر شده از فرانت‌اند (به صورت JSON).
     * @return ResponseEntity با وضعیت HTTP مناسب.
     */
    @PostMapping("/create") // POST /api/admin/menus/create
    public ResponseEntity<?> createMenu(@RequestBody MenuCreateRequest request) { // دریافت Body به صورت JSON
        try {
            menuService.saveMenu(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("منو با موفقیت ایجاد شد!"); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطای سیستمی در ایجاد منو: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    // TODO: می‌توانید متدهای دیگری برای مدیریت منوها (مثل لیست کردن، ویرایش) با API اینجا اضافه کنید.
    // (این متدها مشابه MenuController هستند، اما این یک کنترلر اختصاصی برای ادمین است)
}