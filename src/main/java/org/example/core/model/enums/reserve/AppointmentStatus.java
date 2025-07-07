package org.example.core.model.enums.reserve;

/**
 * این Enum وضعیت‌های مختلف یک رزرو را تعریف می‌کند.
 */
public enum AppointmentStatus {
    PENDING,    // در انتظار تایید توسط متخصص
    CONFIRMED,  // تایید شده
    COMPLETED,  // انجام شده
    CANCELLED   // لغو شده (توسط مشتری یا متخصص)
}