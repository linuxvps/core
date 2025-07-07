package org.example.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * این DTO از CreateUserRequest ارث‌بری کرده و فیلدهای مخصوص متخصص را اضافه می‌کند.
 */
@Getter
@Setter
public class CreateProfessionalRequest extends CreateUserRequest {
    private String specialty; // تخصص اولیه
    private String bio;       // بیوگرافی اولیه
    private Integer consultationDurationMinutes; // مدت زمان هر جلسه مشاوره به دقیقه
    private BigDecimal consultationPrice; // هزینه هر جلسه مشاوره
    private String profilePictureUrl; // آدرس URL عکس پروفایل

}