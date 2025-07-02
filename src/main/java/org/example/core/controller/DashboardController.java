package org.example.core.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("isAuthenticated()")
public class DashboardController {



    @GetMapping
    public String getAllUsers() {
        return "dashboard";
    }


}