package org.example.amlak.repository;

import org.example.amlak.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // اگر می‌خواهید منوهایی را بر اساس نام پرمیشن (مثلاً "VIEW_DASHBOARD") فیلتر کنید،
    // باید از findByRequiredPermission_NameIn استفاده کنید.
    // این متد جایگزین findByPermissionIn می‌شود.
    List<Menu> findByRequiredPermission_NameIn(List<String> permissionNames);

    // اگر متدی برای پیدا کردن منو بر اساس یک نام پرمیشن خاص لازم دارید (که در MenuService هم نیاز بود)
    Optional<Menu> findByRequiredPermission_Name(String permissionName);

    // **مهم:** متد زیر (که باعث خطا می‌شود) را حذف کنید یا کامنت کنید:
    // List<Menu> findByPermissionIn(List<String> permissions); // این خط باعث خطا می‌شود و باید حذف/کامنت شود
    // Optional<Menu> findByPermission(String permission); // این خط هم باعث خطا می‌شود و باید حذف/کامنت شود
}