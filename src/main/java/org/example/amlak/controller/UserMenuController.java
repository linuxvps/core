package org.example.amlak.controller;

import org.example.amlak.dto.MenuDto;
import org.example.amlak.service.UserMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-menus")
public class UserMenuController {

    @Autowired
    private UserMenuService userMenuService;

    // دریافت منوهای اختصاص‌داده‌شده به یک کاربر
    @GetMapping("/{userId}")
    public List<MenuDto> getMenusForUser(@PathVariable Long userId) {
        return userMenuService.getMenusForUser(userId);
    }

    // اختصاص دادن منو به کاربر
    @PostMapping("/{userId}")
    public ResponseEntity<?> assignMenuToUser(@PathVariable Long userId, @RequestBody MenuDto dto) {
        userMenuService.assignMenuToUser(userId, dto);
        return ResponseEntity.ok().build();
    }
}
