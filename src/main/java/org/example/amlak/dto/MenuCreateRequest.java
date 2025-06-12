// 📁 org/example/amlak/dto/MenuCreateRequest.java
package org.example.amlak.dto;

public class MenuCreateRequest {
    private String title;
    private String url;
    private String requiredPermissionName; // نام مجوز مورد نیاز (به صورت String)
    private Integer orderIndex;

    // Constructors (Optional, but good practice)
    public MenuCreateRequest() {}

    public MenuCreateRequest(String title, String url, String requiredPermissionName, Integer orderIndex) {
        this.title = title;
        this.url = url;
        this.requiredPermissionName = requiredPermissionName;
        this.orderIndex = orderIndex;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getRequiredPermissionName() { return requiredPermissionName; }
    public void setRequiredPermissionName(String requiredPermissionName) { this.requiredPermissionName = requiredPermissionName; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}