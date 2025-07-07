package org.example.core.controller.reserve;


import org.example.core.dto.reserve.AvailabilityDto;
import org.example.core.dto.reserve.ProfessionalProfileDto;
import org.example.core.service.ProfessionalService;
import org.example.core.service.UserService; // فرض می‌کنیم متدهای پروفایل در اینجا هستند
import org.example.core.service.reserve.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<Page<ProfessionalProfileDto>> getAllProfessionals(Pageable pageable) {
        Page<ProfessionalProfileDto> professionalsPage = professionalService.getAllProfessionals(pageable);
        return ResponseEntity.ok(professionalsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalProfileDto> getProfessionalById(@PathVariable Long id) {
        ProfessionalProfileDto professional = professionalService.getProfessionalById(id);
        return ResponseEntity.ok(professional);
    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LocalTime> slots = bookingService.getAvailableSlots(id, date);
        return ResponseEntity.ok(slots);
    }

    @PostMapping("/me/availability")
    @PreAuthorize("hasRole('PROFESSIONAL')")
    public ResponseEntity<?> setAvailability(Principal principal, @RequestBody List<AvailabilityDto> availabilities) {
        // شما باید متد setAvailabilityForProfessional را در BookingService پیاده‌سازی کنید
        bookingService.setAvailabilityForProfessional(principal.getName(), availabilities);
        return ResponseEntity.ok("Availability updated successfully.");
    }

}
