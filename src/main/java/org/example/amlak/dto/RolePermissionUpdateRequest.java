// 📁 org/example/amlak/dto/RolePermissionUpdateRequest.java
package org.example.amlak.dto;

public class RolePermissionUpdateRequest {
    private String roleName;
    private String permissionName;

    public RolePermissionUpdateRequest() {}

    public RolePermissionUpdateRequest(String roleName, String permissionName) {
        this.roleName = roleName;
        this.permissionName = permissionName;
    }

    // Getters and Setters
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}