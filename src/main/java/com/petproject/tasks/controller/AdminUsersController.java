package com.petproject.tasks.controller;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminUsersController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String showUsersPage(@RequestParam(value = "username", required = false) String username,
                                @RequestParam(value = "role", required = false) String role,
                                Model model) {
        List<UserDto> users = userService.getFilteredUsers(username, role);
        List<String> allRoles = Arrays.asList(UserRole.ADMIN.name(), UserRole.USER.name());
        model.addAttribute("users", users);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("userDto", new UserDto());
        return "users";
    }

    @PostMapping("/users/create")
    public String createNewUser(@Validated(UserDto.OnCreate.class) @ModelAttribute UserDto userDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("userDto", userDto);
            model.addAttribute("hasErrors", true);
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
                             @Validated(UserDto.OnUpdate.class) @ModelAttribute("userDto") UserDto userDto,
                             BindingResult bindingResult,
                             Model model) {
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            userDto.setPassword(null);
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("userDto", userDto);
            model.addAttribute("updateUserId", userId);
            return "users";
        }

        userService.updateUser(userId, userDto);
        return "redirect:/admin/users";
    }
}