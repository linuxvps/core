// 📁 org/example/amlak/dto/PermissionCreateRequest.java
package org.example.amlak.dto;

public class PermissionCreateRequest {
    private String name; // نام مجوز (مثال: VIEW_PRODUCTS, CREATE_ORDER)

    public PermissionCreateRequest() {}

    public PermissionCreateRequest(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}