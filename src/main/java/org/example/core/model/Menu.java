package org.example.core.model;

import jakarta.persistence.*;

@Entity
public class Menu {
    @Id @GeneratedValue
    private Long id;

    private String title;
    private String url;

    @ManyToOne // یک منو می‌تواند به یک مجوز نیاز داشته باشد
    @JoinColumn(name = "required_permission_id") // نام ستون کلید خارجی در جدول menu
    private Permission requiredPermission; // نام فیلد تغییر کرد برای وضوح بیشتر

    private Integer orderIndex;

    public Long getId() {
        return id;
        //
    }

    public void setId(Long id) {
        this.id = id;
        //
    }

    public String getTitle() {
        return title;
        //
    }

    public void setTitle(String title) {
        this.title = title;
        //
    }

    public String getUrl() {
        return url;
        //
    }

    public void setUrl(String url) {
        this.url = url;
        //
    }

    // گتتر و ستتر جدید برای requiredPermission
    public Permission getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(Permission requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public Integer getOrderIndex() {
        return orderIndex;
        //
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
        //
    }
}