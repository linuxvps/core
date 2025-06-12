package org.example.amlak.controller;

import org.example.amlak.dto.MenuCreateRequest; // DTO را ایمپورت کنید
import org.example.amlak.model.Menu;
import org.example.amlak.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException; // برای استفاده در مدیریت خطا

@RestController
@RequestMapping("/api/menus")
// این سطح دسترسی کلی برای کل کنترلر است.
// هر متدی که PreAuthorize خودش را نداشته باشد، از این پیروی می‌کند.
@PreAuthorize("isAuthenticated()") // فقط کاربران احراز هویت شده می‌توانند به این API دسترسی داشته باشند
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * نمایش منوهای مجاز برای کاربر لاگین‌شده فعلی.
     * @param authentication اطلاعات احراز هویت کاربر جاری.
     * @return لیستی از منوهای قابل دسترسی برای کاربر لاگین شده.
     */
    @GetMapping("/my") // GET /api/menus/my
    public List<Menu> getMyMenus(Authentication authentication) {
        return menuService.getMenusForUser(authentication);
    }

    /**
     * ایجاد یک منوی جدید. فقط توسط مدیران قابل انجام است.
     * @param request شیء MenuCreateRequest شامل عنوان، URL و نام مجوز مورد نیاز.
     * @return ResponseEntity با وضعیت OK در صورت موفقیت یا BAD_REQUEST در صورت خطا.
     */
    @PostMapping // POST /api/menus
    @PreAuthorize("hasRole('ADMIN')") // فقط ادمین‌ها می‌توانند منو ایجاد کنند
    public ResponseEntity<?> createMenu(@RequestBody MenuCreateRequest request) { // تغییر به DTO
        try {
            menuService.saveMenu(request);
            return ResponseEntity.ok().body("منو با موفقیت ایجاد شد.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در ایجاد منو رخ داد: " + e.getMessage());
        }
    }

    /**
     * حذف یک منو بر اساس شناسه. فقط توسط مدیران قابل انجام است.
     * @param id شناسه منو برای حذف.
     * @return ResponseEntity با وضعیت OK در صورت موفقیت یا NOT_FOUND در صورت عدم وجود منو.
     */
    @DeleteMapping("/{id}") // DELETE /api/menus/{id}
    @PreAuthorize("hasRole('ADMIN')") // فقط ادمین‌ها می‌توانند منو حذف کنند
    public ResponseEntity<?> deleteMenu(@PathVariable Long id) {
        try {
            menuService.deleteMenu(id);
            return ResponseEntity.ok().body("منو با موفقیت حذف شد.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در حذف منو رخ داد: " + e.getMessage());
        }
    }

    /**
     * دریافت لیست تمام منوها. فقط توسط مدیران قابل انجام است.
     * @return لیستی از تمام منوها.
     */
    @GetMapping // GET /api/menus
    @PreAuthorize("hasRole('ADMIN')") // فقط ادمین‌ها باید تمام منوها را ببینند
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }

    /**
     * متد جدید: نمایش منوهای قابل دسترسی برای یک کاربر خاص (توسط ادمین).
     * این متد برای فرانت‌اند شما در صفحه مدیریت کاربران نیاز است تا ادمین بتواند منوهای
     * یک کاربر دیگر را بر اساس نقش‌ها و مجوزهای آن کاربر مشاهده کند.
     * @param userId شناسه کاربری که می‌خواهیم منوهای قابل دسترسی او را ببینیم.
     * @return لیستی از منوهای قابل دسترسی برای کاربر مشخص شده.
     */
    @GetMapping("/for-user/{userId}") // GET /api/menus/for-user/{userId}
    @PreAuthorize("hasRole('ADMIN')") // فقط ادمین می‌تواند منوهای کاربران دیگر را ببیند
    public ResponseEntity<?> getMenusForSpecificUser(@PathVariable Long userId) {
        try {
            List<Menu> menus = menuService.getMenusForSpecificUser(userId); // این متد را باید در MenuService اضافه کنید
            return ResponseEntity.ok(menus);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در دریافت منوهای کاربر رخ داد: " + e.getMessage());
        }
    }
}