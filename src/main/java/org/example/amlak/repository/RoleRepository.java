package org.example.amlak.repository;

// 📁 repository/RoleRepository.java

import org.example.amlak.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // اضافه کردن این ایمپورت

import java.util.List;
import java.util.Optional; // اضافه کردن این ایمپورت برای Optional

@Repository // برای اینکه Spring این را به عنوان یک Repository شناسایی کند
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByNameIn(List<String> names);

    // متد جدید برای پیدا کردن یک Role بر اساس نام
    Optional<Role> findByName(String name);
}