package org.example.amlak.config;

import org.example.amlak.model.Menu;
import org.example.amlak.repository.MenuRepository;
import org.example.amlak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserService userService;
    @Autowired private MenuRepository menuRepository;

    @Override
    public void run(String... args) {
        userService.createDefaultAdminIfNotExists();

        if (menuRepository.count() == 0) {
            Menu m1 = new Menu();
            m1.setTitle("مدیریت کاربران");
            m1.setUrl("/admin/user-list.html");
            m1.setPermission("ROLE_ADMIN");
            m1.setOrderIndex(1);

            Menu m2 = new Menu();
            m2.setTitle("مدیریت منوها");
            m2.setUrl("/admin/menu-list.html");
            m2.setPermission("ROLE_ADMIN");
            m2.setOrderIndex(2);

            Menu m3 = new Menu();
            m3.setTitle("داشبورد");
            m3.setUrl("/dashboard.html");
            m3.setPermission("ROLE_ADMIN");
            m3.setOrderIndex(3);

            menuRepository.saveAll(Arrays.asList(m1, m2, m3));

            System.out.println("✅ منوهای اولیه اضافه شدند.");
        }
    }
}
