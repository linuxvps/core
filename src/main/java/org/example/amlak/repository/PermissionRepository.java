package org.example.amlak.repository;

import org.example.amlak.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {}
