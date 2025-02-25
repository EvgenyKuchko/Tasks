package com.petproject.tasks.controller;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showAdminPage() {
        return "admin";
    }

    @GetMapping("/users")
    public String showUsersPage(@RequestParam(value = "username", required = false) String username,
                                @RequestParam(value = "role", required = false) String role,
                                Model model) {
        List<UserDto> users = userService.getAllUsers();
        List<String> allRoles = new ArrayList<>();
        allRoles.add(UserRole.ADMIN.name());
        allRoles.add(UserRole.USER.name());

        if (username != null && !username.isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (role != null && !role.isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getRoles().stream()
                            .anyMatch(userRole -> userRole.name().equalsIgnoreCase(role)))
                    .collect(Collectors.toList());
        }

        model.addAttribute("users", users);
        model.addAttribute("allRoles", allRoles); // Передаем роли в модель
        return "users";
    }
}