package org.example.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * کلاس Professional که با Lombok بازنویسی شده و "مالک" رابطه یک-به-یک با User است.
 */
@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professional {

    @Id
    private Long id; // این ID همان ID کاربر در جدول users خواهد بود

    /**
     * رابطه یک-به-یک با User.
     * MapsId مشخص می‌کند که ID این جدول (که کلید اصلی است) توسط رابطه با User مدیریت می‌شود.
     * این کار باعث می‌شود که id در این جدول، هم کلید اصلی و هم کلید خارجی باشد.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(length = 100)
    private String specialty; // تخصص (مثلاً: وکیل خانواده)

    @Column(columnDefinition = "TEXT")
    private String bio; // بیوگرافی یا توضیحات درباره متخصص

    @Column(name = "consultation_duration_minutes")
    private Integer consultationDurationMinutes; // مدت زمان هر جلسه مشاوره به دقیقه

    @Column(name = "consultation_price")
    private BigDecimal consultationPrice; // هزینه هر جلسه مشاوره

    @Column(name = "profile_picture_url")
    private String profilePictureUrl; // آدرس URL عکس پروفایل
}
