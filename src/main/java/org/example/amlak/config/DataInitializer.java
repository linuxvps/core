package org.example.amlak.config;

import org.example.amlak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserService userService;

    @Override
    public void run(String... args) {
        userService.createDefaultAdminIfNotExists();
    }
}
