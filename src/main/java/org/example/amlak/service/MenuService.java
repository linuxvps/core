package org.example.amlak.service;

import org.example.amlak.model.Menu;
import org.example.amlak.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    // نمایش منوهای مجاز کاربر فعلی
    public List<Menu> getMenusForUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }

        List<String> permissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return menuRepository.findByPermissionIn(permissions);
    }

    // معادل بالا ولی بدون نیاز به Authentication مستقیم (مثلاً از Controller پاس داده می‌شه)
    public List<Menu> getMenusByPermissions(List<String> permissions) {
        return menuRepository.findByPermissionIn(permissions);
    }

    // ذخیره منو جدید
    public void saveMenu(Menu menu) {
        Optional<Menu> existing = menuRepository.findByPermission(menu.getPermission());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("این سطح دسترسی قبلاً برای یک منو ثبت شده است.");
        }
        menuRepository.save(menu);
    }

    // حذف منو
    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    // نمایش همه منوها برای ادمین
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }
}
