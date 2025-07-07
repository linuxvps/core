package org.example.core.service.reserve;

import org.example.core.dto.reserve.AvailabilityDto;
import org.example.core.model.Professional;
import org.example.core.model.User;
import org.example.core.model.reserve.Appointment;
import org.example.core.model.reserve.Availability;
import org.example.core.repository.UserRepository;
import org.example.core.repository.reserve.AppointmentRepository;
import org.example.core.repository.reserve.AvailabilityRepository;
import org.example.core.repository.reserve.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional // تمام متدهای این کلاس در یک تراکنش دیتابیس اجرا می‌شوند
public class BookingService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * این متد، لیست بازه‌های زمانی آزاد یک متخصص در یک روز خاص را محاسبه می‌کند.
     * این یکی از پیچیده‌ترین و مهم‌ترین بخش‌های منطق سیستم است.
     * @param professionalId شناسه متخصص.
     * @param date تاریخی که می‌خواهیم وقت‌های آزاد آن را پیدا کنیم.
     * @return لیستی از LocalDateTime که هر کدام یک بازه زمانی آزاد را نشان می‌دهند.
     */
    public List<LocalTime> getAvailableSlots(Long professionalId, LocalDate date) {
        // ۱. پیدا کردن اطلاعات متخصص و ساعات کاری او
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new NoSuchElementException("Professional not found"));

        List<Availability> weeklyAvailabilities = availabilityRepository.findByProfessional(professional.getUser());

        // پیدا کردن ساعت کاری برای روز مشخص شده
        Availability dayAvailability = weeklyAvailabilities.stream()
                .filter(a -> a.getDayOfWeek() == date.getDayOfWeek())
                .findFirst()
                .orElse(null);

        if (dayAvailability == null) {
            return new ArrayList<>(); // اگر در این روز کاری تعریف نشده، لیست خالی برمی‌گردد
        }

        // ۲. پیدا کردن تمام رزروهای قبلی در آن روز
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Appointment> existingAppointments = appointmentRepository.findAppointmentsForProfessionalInDateRange(
                professionalId, startOfDay, endOfDay);

        // ۳. تولید تمام بازه‌های زمانی ممکن و حذف بازه‌های رزرو شده
        List<LocalTime> availableSlots = new ArrayList<>();
        int duration = professional.getConsultationDurationMinutes();
        LocalTime slotTime = dayAvailability.getStartTime();

        while (slotTime.plusMinutes(duration).isBefore(dayAvailability.getEndTime()) || slotTime.plusMinutes(duration).equals(dayAvailability.getEndTime())) {
            final LocalTime currentSlotStart = slotTime;
            final LocalTime currentSlotEnd = slotTime.plusMinutes(duration);

            // بررسی می‌کنیم که آیا این بازه با یکی از رزروهای قبلی تداخل دارد یا نه
            boolean isBooked = existingAppointments.stream()
                    .anyMatch(app -> currentSlotStart.isBefore(app.getStartDateTime().toLocalTime()) && currentSlotEnd.isAfter(app.getStartDateTime().toLocalTime()) ||
                            currentSlotStart.equals(app.getStartDateTime().toLocalTime()) ||
                            (currentSlotStart.isAfter(app.getStartDateTime().toLocalTime()) && currentSlotStart.isBefore(app.getEndDateTime().toLocalTime()))
                    );

            if (!isBooked) {
                availableSlots.add(currentSlotStart);
            }

            slotTime = slotTime.plusMinutes(duration); // حرکت به بازه زمانی بعدی
        }

        return availableSlots;
    }

    /**
     * ساعات کاری هفتگی یک متخصص را ثبت یا به‌روزرسانی می‌کند.
     * این متد ابتدا تمام ساعات کاری قبلی را حذف کرده و سپس لیست جدید را ذخیره می‌کند.
     * @param username نام کاربری متخصص لاگین کرده.
     * @param availabilitiesDto لیستی از ساعات کاری جدید.
     */
    public void setAvailabilityForProfessional(String username, List<AvailabilityDto> availabilitiesDto) {
        // ۱. پیدا کردن کاربر متخصص از طریق نام کاربری
        User professionalUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Professional user not found with username: " + username));

        // ۲. حذف تمام ساعات کاری قبلی این متخصص برای اطمینان از یکپارچگی داده‌ها
        List<Availability> existingAvailabilities = availabilityRepository.findByProfessional(professionalUser);
        availabilityRepository.deleteAll(existingAvailabilities);

        // ۳. تبدیل DTO های دریافتی به Entity های Availability و ذخیره آن‌ها
        List<Availability> newAvailabilities = availabilitiesDto.stream()
                .map(dto -> {
                    Availability availability = new Availability();
                    availability.setProfessional(professionalUser);
                    availability.setDayOfWeek(dto.getDayOfWeek());
                    availability.setStartTime(dto.getStartTime());
                    availability.setEndTime(dto.getEndTime());
                    return availability;
                })
                .collect(Collectors.toList());

        availabilityRepository.saveAll(newAvailabilities);
    }


    // متدهای دیگری مانند createAppointment, confirmAppointment, cancelAppointment
    // در مراحل بعدی به این سرویس اضافه خواهند شد.
}

