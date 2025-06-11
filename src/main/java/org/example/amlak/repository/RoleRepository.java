package org.example.amlak.repository;

// 📁 repository/RoleRepository.java
import org.example.amlak.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {}
