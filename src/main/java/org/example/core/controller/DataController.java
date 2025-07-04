package org.example.core.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class DataController {



    @GetMapping
    public String getAllUsers() {
        return "data";
    }


}
