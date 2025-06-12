package org.example.amlak.service;

import org.example.amlak.model.Menu;
import org.example.amlak.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    // نمایش منوهای مجاز کاربر فعلی
    // این متد باید لیست پرمیشن‌های کاربر را به شکل String (نام پرمیشن) دریافت کند
    // و منوهایی را برگرداند که requiredPermission آن‌ها در لیست پرمیشن‌های کاربر موجود باشد.
    public List<Menu> getMenusForUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }

        // استخراج نام تمام پرمیشن‌هایی که کاربر از طریق نقش‌هایش دارد
        Set<String> userPermissionNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()); // استفاده از Set برای جلوگیری از تکرار و بهبود عملکرد جستجو

        // دریافت تمام منوها
        List<Menu> allMenus = menuRepository.findAll();

        // فیلتر کردن منوها بر اساس پرمیشن‌های کاربر
        List<Menu> accessibleMenus = allMenus.stream()
                .filter(menu -> {
                    // اگر منو نیازی به مجوز خاصی ندارد (requiredPermission == null)، همیشه قابل مشاهده است
                    if (menu.getRequiredPermission() == null) {
                        return true;
                    }
                    // اگر منو به مجوزی نیاز دارد، بررسی می‌کنیم که کاربر آن مجوز را دارد یا خیر
                    return userPermissionNames.contains(menu.getRequiredPermission().getName());
                })
                .sorted(Comparator.comparing(Menu::getOrderIndex, Comparator.nullsLast(Integer::compareTo))) // مرتب‌سازی بر اساس orderIndex، null ها در انتها
                .collect(Collectors.toList());

        return accessibleMenus;
    }

    // ذخیره منو جدید
    // این متد باید اطمینان حاصل کند که یک منو با یک requiredPermission تکراری ذخیره نشود
    // یا می‌توانید این بررسی را حذف کنید اگر یک Permission می‌تواند به چندین منو منجر شود (که منطقی‌تر است)
    // تغییر یافت تا با شیء Permission کار کند
    public Menu saveMenu(Menu menu) {
        // اگر نیازی به منحصر به فرد بودن بر اساس requiredPermission نیست، این خط را حذف کنید.
        // Optional<Menu> existing = menuRepository.findByRequiredPermission(menu.getRequiredPermission());
        // if (existing.isPresent() && !existing.get().getId().equals(menu.getId())) {
        //     throw new IllegalArgumentException("این سطح دسترسی قبلاً برای یک منو ثبت شده است.");
        // }
        // معمولا یک permission میتونه به چندین منو مرتبط باشه. (مثال: هر دو منوی 'User List' و 'Add User' نیاز به PERM_USER_MANAGEMENT دارند)
        // پس خطوط بالا رو میتونید حذف کنید یا منطقشون رو تغییر بدید.

        return menuRepository.save(menu);
    }

    // حذف منو
    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    // نمایش همه منوها برای ادمین (یا کاربرانی با دسترسی خاص)
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }
}