package org.example.core.controller;

import org.example.core.dto.CreateUserRequest;
import org.example.core.dto.UserResponse;
import org.example.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/register") // تغییر مسیر اصلی به /api/users برای استانداردسازی API
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping // POST /api/users
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("user create successfully."); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("خطایی در ایجاد کاربر رخ داد: " + e.getMessage()); // 500 Internal Server Error
        }
    }
}