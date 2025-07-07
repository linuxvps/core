package org.example.core.service;


import org.example.core.dto.reserve.ProfessionalProfileDto;
import org.example.core.model.Professional;
import org.example.core.repository.reserve.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * این سرویس اختصاصی، تمام منطق کسب‌وکار مربوط به متخصصان را مدیریت می‌کند.
 */
@Service
public class ProfessionalService {

    @Autowired
    private ProfessionalRepository professionalRepository;

    /**
     * لیستی صفحه‌بندی شده از تمام متخصصان را برمی‌گرداند.
     * @param pageable اطلاعات صفحه‌بندی.
     * @return یک Page از پروفایل‌های متخصصان.
     */
    public Page<ProfessionalProfileDto> getAllProfessionals(Pageable pageable) {
        return professionalRepository.findProfessionalProfiles(pageable);
    }

    /**
     * اطلاعات پروفایل عمومی یک متخصص خاص را بر اساس ID او برمی‌گرداند.
     * @param id شناسه متخصص.
     * @return اطلاعات پروفایل متخصص.
     * @throws NoSuchElementException اگر متخصصی با این ID یافت نشود.
     */
    public ProfessionalProfileDto getProfessionalById(Long id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Professional not found with ID: " + id));

        // با استفاده از رابطه، به اطلاعات کاربر دسترسی پیدا می‌کنیم
        return new ProfessionalProfileDto(
                professional.getId(),
                professional.getUser().getFirstName(),
                professional.getUser().getLastName(),
                professional.getSpecialty(),
                professional.getBio(),
                professional.getConsultationDurationMinutes(),
                professional.getConsultationPrice(),
                professional.getProfilePictureUrl()
        );
    }


}
