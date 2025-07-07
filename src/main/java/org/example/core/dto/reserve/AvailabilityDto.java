package org.example.core.dto.reserve;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class AvailabilityDto {
    private DayOfWeek dayOfWeek; // روز هفته (MONDAY, TUESDAY, ...)
    private LocalTime startTime; // ساعت شروع
    private LocalTime endTime;   // ساعت پایان
}