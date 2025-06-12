// 📁 org/example/amlak/service/MenuService.java
package org.example.amlak.service;

import org.example.amlak.dto.MenuCreateRequest; // DTO جدید را ایمپورت کنید
import org.example.amlak.model.Menu;
import org.example.amlak.model.Permission;
import org.example.amlak.repository.MenuRepository;
import org.example.amlak.repository.PermissionRepository; // باید PermissionRepository را Autowire کنید
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.example.amlak.model.User; // برای استفاده از User
import org.example.amlak.repository.UserRepository; // برای Autowire کردن UserRepository
import java.util.NoSuchElementException; // برای مدیریت خطای کاربر یافت نشد

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;
    @Autowired // اضافه کردن PermissionRepository
    private PermissionRepository permissionRepository;
    @Autowired
    private UserRepository userRepository; // اضافه کردن UserRepository



    public List<Menu> getMenusForUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }

        Set<String> userPermissionNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        List<Menu> allMenus = menuRepository.findAll();

        List<Menu> accessibleMenus = allMenus.stream()
                .filter(menu -> {
                    if (menu.getRequiredPermission() == null) {
                        return true;
                    }
                    return userPermissionNames.contains(menu.getRequiredPermission().getName());
                })
                .sorted(Comparator.comparing(Menu::getOrderIndex, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        return accessibleMenus;
    }

    // متد saveMenu اصلاح شده برای دریافت DTO
    public Menu saveMenu(MenuCreateRequest request) {
        Menu menu = new Menu();
        menu.setTitle(request.getTitle());
        menu.setUrl(request.getUrl());
        menu.setOrderIndex(request.getOrderIndex());

        // اگر نام مجوزی ارسال شده باشد، آن را پیدا کرده و به منو اختصاص دهید
        if (request.getRequiredPermissionName() != null && !request.getRequiredPermissionName().isEmpty()) {
            Permission requiredPermission = permissionRepository.findByName(request.getRequiredPermissionName())
                    .orElseThrow(() -> new IllegalArgumentException("مجوز با نام " + request.getRequiredPermissionName() + " یافت نشد."));
            menu.setRequiredPermission(requiredPermission);
        } else {
            // اگر مجوزی نیاز نیست (مثلاً برای منوهای عمومی)
            menu.setRequiredPermission(null);
        }

        return menuRepository.save(menu);
    }

    // حذف منو
    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new NoSuchElementException("منویی با شناسه " + id + " یافت نشد.");
        }
        menuRepository.deleteById(id);
    }

    // نمایش همه منوها برای ادمین
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }


    public List<Menu> getMenusForSpecificUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("کاربری با شناسه " + userId + " یافت نشد."));

        if (!user.isEnabled()) {
            return Collections.emptyList(); // اگر کاربر غیرفعال است، هیچ منویی برنگردان
        }

        Set<String> userPermissionNames = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        List<Menu> allMenus = menuRepository.findAll();

        return allMenus.stream()
                .filter(menu -> {
                    if (menu.getRequiredPermission() == null) {
                        return true;
                    }
                    return userPermissionNames.contains(menu.getRequiredPermission().getName());
                })
                .sorted(Comparator.comparing(Menu::getOrderIndex, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
    }
}