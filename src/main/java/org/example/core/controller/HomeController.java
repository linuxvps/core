package org.example.core.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index.html";
    }
}