package org.example.core.model.reserve;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.core.model.User;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * این Entity ساعات کاری هفتگی هر متخصص را تعریف می‌کند.
 */
@Entity
@Table(name = "availabilities")
@Getter
@Setter
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_user_id", nullable = false)
    private User professional; // متخصصی که این ساعت کاری متعلق به اوست

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // روز هفته (MONDAY, TUESDAY, ...)

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // ساعت شروع کار

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // ساعت پایان کار
}
