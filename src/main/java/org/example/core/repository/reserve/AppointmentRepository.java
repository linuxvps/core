package org.example.core.repository.reserve;


import org.example.core.model.User;
import org.example.core.model.reserve.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * این Repository عملیات مربوط به دیتابیس برای Entity رزرو (Appointment) را مدیریت می‌کند.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * تمام رزروهای یک متخصص خاص را پیدا می‌کند.
     * @param professional متخصصی که رزروها متعلق به اوست.
     * @return لیستی از رزروها.
     */
    List<Appointment> findByProfessional(User professional);

    /**
     * تمام رزروهای یک مشتری خاص را پیدا می‌کند.
     * @param client مشتری که رزروها را انجام داده.
     * @return لیستی از رزروها.
     */
    List<Appointment> findByClient(User client);

    /**
     * تمام رزروهای یک متخصص در یک بازه زمانی مشخص را پیدا می‌کند.
     * این متد برای پیدا کردن "بازه‌های زمانی آزاد" بسیار حیاتی است.
     * @param professionalId شناسه متخصص.
     * @param startDateTime زمان شروع بازه.
     * @param endDateTime زمان پایان بازه.
     * @return لیستی از رزروهای موجود در آن بازه.
     */
    @Query("SELECT a FROM Appointment a WHERE a.professional.id = :professionalId AND a.startDateTime < :endDateTime AND a.endDateTime > :startDateTime")
    List<Appointment> findAppointmentsForProfessionalInDateRange(
            @Param("professionalId") Long professionalId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
