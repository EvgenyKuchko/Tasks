package com.petproject.tasks.controller;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminUsersController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String showUsersPage(@RequestParam(value = "username", required = false) String username,
                                @RequestParam(value = "role", required = false) String role,
                                Model model) {
        UserDto userDto = new UserDto();
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
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("userDto", userDto);
        return "users";
    }

    @PostMapping("/users/create")
    public String createNewUser(@ModelAttribute UserDto userDto) {
        userService.registerUser(userDto);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}")
    public String addOrRemoveAdminRole(@PathVariable("userId") Long userId) {
        userService.addOrRemoveAdminRole(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUserByUserId(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/update")
    public String updateUser(@PathVariable("userId") Long userId, @ModelAttribute UserDto userDto) {
        userService.updateUser(userId, userDto);
        return "redirect:/admin/users";
    }
}