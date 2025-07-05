package org.example.core.controller;

import org.example.core.dto.CreateUserRequest;
import org.example.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/register") // تغییر مسیر اصلی به /api/users برای استانداردسازی API
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> register(@RequestBody CreateUserRequest request) {
        try {
            request.setRoles(List.of("ROLE_USER"));
            userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("user create successfully."); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while creating the user :" + e.getMessage()); // 500 Internal Server Error
        }
    }

    // به جای /lawyer از یک نام عمومی‌تر استفاده می‌کنیم
    @PostMapping("/professional")
    public ResponseEntity<?> registerProfessional(@RequestBody CreateUserRequest request) {
        try {
            // نقش کاربر را نیز به نام عمومی جدید تغییر می‌دهیم
            request.setRoles(List.of("ROLE_PROFESSIONAL"));
            userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Professional user registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while creating the user: " + e.getMessage());
        }
    }
}