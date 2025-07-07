package org.example.core.dto.reserve;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * این DTO برای نمایش اطلاعات عمومی یک متخصص به مشتریان استفاده می‌شود.
 * این کلاس شامل اطلاعات حساس نیست.
 */
@Getter
@Setter
@AllArgsConstructor
public class ProfessionalProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;
    private String bio;
    private Integer consultationDurationMinutes;
    private BigDecimal consultationPrice;
    private String profilePictureUrl;
}