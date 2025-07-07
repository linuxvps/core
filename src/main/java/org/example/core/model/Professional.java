// Professional.java (تغییرات اصلی)
package org.example.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * کلاس Professional که با Lombok بازنویسی شده و دارای ID مستقل و یک userId برای ارتباط است.
 */
@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // این ID، کلید اصلی مستقل و خودکار برای خود Professional است.

    /**
     * رابطه یک-به-یک با User.
     * @JoinColumn("user_id") ایجاد یک ستون 'user_id' در جدول professionals می‌کند که
     * به 'id' جدول users ارجاع می‌دهد (کلید خارجی).
     * این کلاس "مالک" رابطه است زیرا شامل JoinColumn است.
     * FetchType.LAZY برای بارگذاری تأخیری User.
     * optional = false به این معنی است که هر Professional باید یک User مرتبط داشته باشد.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true) // 'user_id' اینجا کلید خارجی است. 'unique = true' برای تضمین یک به یک بودن
    private User user; // این فیلد نماینده User مرتبط است و پشت صحنه به user_id در دیتابیس مپ می‌شود.

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