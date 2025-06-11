package org.example.amlak.repository;

// 📁 repository/RoleRepository.java

import org.example.amlak.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByNameIn(List<String> names);

}
