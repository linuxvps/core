package org.example.amlak.repository;

import org.example.amlak.model.UserMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMenuRepository extends JpaRepository<UserMenu, Long> {
    List<UserMenu> findByUserId(Long userId);
}