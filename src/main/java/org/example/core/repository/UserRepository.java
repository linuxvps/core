package org.example.core.repository;

import org.example.core.dto.reserve.ProfessionalProfileDto;
import org.example.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);


    /**
     * این کوئری سفارشی، با استفاده از JOIN، اطلاعات را از دو جدول User و Professional
     * به صورت همزمان خوانده و مستقیماً به DTO تبدیل می‌کند.
     * این کار از مشکل عملکردی N+1 جلوگیری می‌کند.
     */

    @Query("SELECT new org.example.core.dto.reserve.ProfessionalProfileDto(" +
            "p.id, p.user.firstName, p.user.lastName, p.specialty, p.bio, " +
            "p.consultationDurationMinutes, p.consultationPrice, p.profilePictureUrl) " +
            "FROM Professional p JOIN p.user u")
    Page<ProfessionalProfileDto> findProfessionalProfiles(Pageable pageable);
}