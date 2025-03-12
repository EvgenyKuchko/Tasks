package com.petproject.tasks.controller;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        model.addAttribute("userDto", new UserDto());
        return "users";
    }

    @PostMapping("/users/create")
    public String createNewUser(@Valid @ModelAttribute UserDto userDto,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("userDto", userDto);
            return "users";
        }
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
    public String updateUser(@PathVariable Long userId,
                             @Valid @ModelAttribute("userDto") UserDto userDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("userDto", userDto);
            return "users";
        }

        userService.updateUser(userId, userDto);
        return "redirect:/admin/users";
    }
}