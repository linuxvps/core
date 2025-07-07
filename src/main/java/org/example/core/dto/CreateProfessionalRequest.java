package org.example.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * این DTO از CreateUserRequest ارث‌بری کرده و فیلدهای مخصوص متخصص را اضافه می‌کند.
 */
@Getter
@Setter
public class CreateProfessionalRequest extends CreateUserRequest {
    private String specialty; // تخصص اولیه
    private String bio;       // بیوگرافی اولیه
}