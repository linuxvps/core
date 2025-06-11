package org.example.amlak.service;

import org.example.amlak.dto.MenuDto;
import org.example.amlak.model.Menu;
import org.example.amlak.model.User;
import org.example.amlak.model.UserMenu;
import org.example.amlak.repository.MenuRepository;
import org.example.amlak.repository.UserMenuRepository;
import org.example.amlak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserMenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserMenuRepository userMenuRepository;

    @Autowired
    private UserRepository userRepository;

    public List<MenuDto> getMenusForUser(Long userId) {
        List<UserMenu> userMenus = userMenuRepository.findByUserId(userId);

        return userMenus.stream().map(um -> {
            Menu m = um.getMenu();
            MenuDto dto = new MenuDto();
            dto.setTitle(m.getTitle());
            dto.setUrl(m.getUrl());
            dto.setPermission(m.getPermission());
            return dto;
        }).collect(Collectors.toList());
    }

    public void assignMenuToUser(Long userId, MenuDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Menu> optionalMenu = menuRepository.findByPermission(dto.getPermission());

        Menu menu;
        if (optionalMenu.isPresent()) {
            menu = optionalMenu.get();
        } else {
            menu = new Menu();
            menu.setTitle(dto.getTitle());
            menu.setUrl(dto.getUrl());
            menu.setPermission(dto.getPermission());
            menu = menuRepository.save(menu);
        }

        UserMenu userMenu = new UserMenu();
        userMenu.setUser(user);
        userMenu.setMenu(menu);
        userMenuRepository.save(userMenu);
    }
}
