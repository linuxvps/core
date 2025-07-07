package org.example.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.core.model.enums.UserType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter // به صورت خودکار تمام متدهای getter را برای فیلدها ایجاد می‌کند
@Setter // به صورت خودکار تمام متدهای setter را برای فیلدها ایجاد می‌کند
@NoArgsConstructor // یک constructor بدون آرگومان (که برای JPA لازم است) ایجاد می‌کند
@AllArgsConstructor // یک constructor با تمام فیلدها به عنوان آرگومان ایجاد می‌کند
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    /**
     * رابطه یک-به-یک با Professional.
     * mappedBy = "user" مشخص می‌کند که مدیریت این رابطه توسط فیلد 'user' در کلاس Professional انجام می‌شود.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Professional professional;
}