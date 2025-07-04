package org.example.core.repository;

import org.example.core.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // اضافه کردن این ایمپورت

import java.util.List;
import java.util.Optional; // اضافه کردن این ایمپورت برای کار با Optional
import java.util.Set;

@Repository // این انوتیشن برای صراحت بیشتر و استفاده از ویژگی‌های Spring Component Scan خوب است
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // این متد برای پیدا کردن یک Permission بر اساس نام آن (مثلاً "VIEW_DASHBOARD") استفاده می‌شود.
    // بازگرداندن Optional به شما کمک می‌کند تا حالت عدم یافتن Permission را به درستی مدیریت کنید.
    Optional<Permission> findByName(String name);

    Set<Permission> findByNameIn(List<String> names);

}