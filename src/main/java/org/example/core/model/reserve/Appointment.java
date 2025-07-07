package org.example.core.model.reserve;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.core.model.User;
import org.example.core.model.enums.reserve.AppointmentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user_id", nullable = false)
    private User client; // کاربری که وقت را رزرو کرده (مشتری)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_user_id", nullable = false)
    private User professional; // متخصصی که وقت از او گرفته شده

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDateTime; // زمان و تاریخ دقیق شروع جلسه

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDateTime; // زمان و تاریخ دقیق پایان جلسه

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status; // وضعیت رزرو (مثلاً: در انتظار تایید، تایید شده، لغو شده)

    @Column(columnDefinition = "TEXT")
    private String notes; // یادداشت‌های مشتری هنگام رزرو
}

