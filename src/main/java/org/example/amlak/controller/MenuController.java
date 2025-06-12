package org.example.amlak.controller;

import org.example.amlak.model.Menu;
import org.example.amlak.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // نمایش منوهای مجاز برای کاربر لاگین‌شده
    @GetMapping("/my")
    public List<Menu> getMyMenus(Authentication authentication) {
        return menuService.getMenusForUser(authentication);
    }

    // فقط ادمین‌ها بتونن منو اضافه کنن
    @PostMapping
    public ResponseEntity<?> createMenu(@RequestBody Menu menu) {
        menuService.saveMenu(menu);
        return ResponseEntity.ok().build();
    }

    // حذف منو
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok().build();
    }

    // لیست همه منوها برای ادمین
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }
}
