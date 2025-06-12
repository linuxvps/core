package org.example.amlak.controller;

import org.example.amlak.dto.CreateUserRequest;
import org.example.amlak.dto.UserResponse;
import org.example.amlak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // برای دریافت نام نقش از JSON
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users") // تغییر مسیر اصلی به /api/users برای استانداردسازی API
@PreAuthorize("hasRole('ADMIN')") // تمام متدهای این کنترلر فقط برای ADMIN قابل دسترسی است
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @return لیستی از تمام کاربران با اطلاعات خلاصه.
     */
    @GetMapping // GET /api/users
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * ایجاد یک کاربر جدید.
     * @param request درخواست شامل نام کاربری، رمز عبور و نقش‌ها.
     * @return ResponseEntity با وضعیت OK در صورت موفقیت یا BAD_REQUEST در صورت خطا.
     */
    @PostMapping // POST /api/users
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("کاربر با موفقیت ایجاد شد."); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در ایجاد کاربر رخ داد: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    /**
     * حذف یک کاربر بر اساس شناسه.
     * @param id شناسه کاربر برای حذف.
     * @return ResponseEntity با وضعیت OK در صورت موفقیت یا NOT_FOUND در صورت عدم وجود کاربر.
     */
    @DeleteMapping("/{id}") // DELETE /api/users/{id}
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("کاربر با موفقیت حذف شد.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در حذف کاربر رخ داد: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    /**
     * @param authentication اطلاعات احراز هویت کاربر جاری.
     * @return لیستی از نام نقش‌های کاربر لاگین شده.
     */
    @GetMapping("/me/roles") // GET /api/users/me/roles
    // این اندپوینت برای کاربر لاگین شده است و نیاز به نقش ADMIN ندارد،
    // مگر اینکه فقط ADMIN بتواند نقش‌های خودش را ببیند.
    // اگر قرار است هر کاربری نقش‌های خودش را ببیند، می‌توانید PreAuthorize را بردارید یا به hasAnyRole('USER', 'ADMIN') تغییر دهید
    @PreAuthorize("isAuthenticated()") // فقط کاربرانی که لاگین کرده‌اند می‌توانند به این اندپوینت دسترسی داشته باشند.
    public List<String> getMyRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * متد جدید: دریافت نقش‌های یک کاربر خاص (بر اساس userId).
     * این متد برای فرانت‌اند شما در صفحه مدیریت کاربران نیاز است.
     * @param userId شناسه کاربر مورد نظر.
     * @return لیستی از نام نقش‌های کاربر.
     */
    @GetMapping("/{userId}/roles") // GET /api/users/{userId}/roles
    // این متد فقط باید توسط ادمین قابل دسترسی باشد
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserRoles(@PathVariable Long userId) {
        try {
            List<String> roles = userService.getUserRoleNames(userId);
            return ResponseEntity.ok(roles);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در دریافت نقش‌های کاربر رخ داد: " + e.getMessage());
        }
    }

    /**
     * متد جدید: اختصاص (افزودن) یک نقش به کاربر خاص.
     * این متد برای فرانت‌اند شما در صفحه مدیریت کاربران نیاز است.
     * @param userId شناسه کاربر مورد نظر.
     * @param payload شامل "roleName" نام نقشی که باید اضافه شود.
     * @return وضعیت OK در صورت موفقیت.
     */
    @PostMapping("/{userId}/roles") // POST /api/users/{userId}/roles
    // این متد فقط باید توسط ادمین قابل دسترسی باشد
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRoleToUser(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String roleName = payload.get("roleName");
        if (roleName == null || roleName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("نام نقش نمی‌تواند خالی باشد.");
        }
        try {
            userService.addRoleToUser(userId, roleName);
            return ResponseEntity.ok().body("نقش '" + roleName + "' با موفقیت به کاربر " + userId + " اضافه شد.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // کاربر یا نقش یافت نشد
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // نقش قبلاً موجود است یا نامعتبر است
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در اضافه کردن نقش رخ داد: " + e.getMessage());
        }
    }

     @DeleteMapping("/{userId}/roles/{roleName}")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<?> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName) {
         try {
             userService.removeRoleFromUser(userId, roleName); // نیاز به پیاده‌سازی این متد در UserService
             return ResponseEntity.ok().body("نقش با موفقیت از کاربر حذف شد.");
         } catch (NoSuchElementException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
         } catch (IllegalArgumentException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }
}