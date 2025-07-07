package org.example.core.repository.reserve;

import org.example.core.dto.reserve.ProfessionalProfileDto;
import org.example.core.model.Professional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * این Repository عملیات مربوط به دیتابیس برای Entity متخصص (Professional) را مدیریت می‌کند.
 * JpaRepository به صورت خودکار متدهای استاندارد CRUD را فراهم می‌کند.
 */
@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    // در آینده می‌توان متدهای جستجوی سفارشی را در اینجا اضافه کرد.
    // مثلا: List<Professional> findBySpecialty(String specialty);


    /**
     * این کوئری سفارشی، با استفاده از JOIN، اطلاعات را از دو جدول Professional و User
     * به صورت همزمان خوانده، مستقیماً به DTO تبدیل کرده و نتیجه را صفحه‌بندی می‌کند.
     * این بهینه‌ترین روش ممکن برای این کار است.
     */
    @Query("SELECT new org.example.core.dto.reserve.ProfessionalProfileDto(" +
            "p.id, p.user.firstName, p.user.lastName, p.specialty, p.bio, " +
            "p.consultationDurationMinutes, p.consultationPrice, p.profilePictureUrl) " +
            "FROM Professional p JOIN p.user u")
    Page<ProfessionalProfileDto> findProfessionalProfiles(Pageable pageable);
}
