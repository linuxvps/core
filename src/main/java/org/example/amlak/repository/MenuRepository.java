package org.example.amlak.repository;

import org.example.amlak.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // پیدا کردن یک منو بر اساس permission خاص
    Optional<Menu> findByPermission(String permission);

    // پیدا کردن لیستی از منوها بر اساس لیستی از permissions
    List<Menu> findByPermissionIn(List<String> permissions);

    // مرتب‌سازی منوها بر اساس ترتیب مشخص‌شده
    List<Menu> findAllByOrderByOrderIndexAsc();
}
