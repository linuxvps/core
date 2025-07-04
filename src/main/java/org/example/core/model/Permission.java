package org.example.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id @GeneratedValue
    private Long id;

    private String name; // مثال: READ_USERS یا MANAGE_PROPERTIES

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
