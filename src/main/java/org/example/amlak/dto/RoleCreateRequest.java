// 📁 org/example/amlak/dto/RoleCreateRequest.java
package org.example.amlak.dto;

import java.util.List;

public class RoleCreateRequest {
    private String name; // نام نقش (مثال: ROLE_MANAGER)
    private List<String> permissionNames; // نام مجوزهای مرتبط با این نقش (مثال: VIEW_DASHBOARD, MANAGE_PROPERTIES)

    public RoleCreateRequest() {}

    public RoleCreateRequest(String name, List<String> permissionNames) {
        this.name = name;
        this.permissionNames = permissionNames;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPermissionNames() {
        return permissionNames;
    }

    public void setPermissionNames(List<String> permissionNames) {
        this.permissionNames = permissionNames;
    }
}