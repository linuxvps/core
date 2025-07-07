package org.example.core.repository.reserve;

import org.example.core.model.User;
import org.example.core.model.reserve.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * این Repository عملیات مربوط به دیتابیس برای Entity ساعات کاری (Availability) را مدیریت می‌کند.
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    /**
     * تمام ساعات کاری تعریف شده برای یک متخصص خاص را پیدا می‌کند.
     *
     * @param professional متخصصی که می‌خواهیم ساعات کاری او را پیدا کنیم.
     * @return لیستی از ساعات کاری.
     */
    List<Availability> findByProfessional(User professional);
}
