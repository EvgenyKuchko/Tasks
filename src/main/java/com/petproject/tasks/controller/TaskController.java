package com.petproject.tasks.controller;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.transformer.UserTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTransformer userTransformer;

    @GetMapping("/tasks")
    public String tasksPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();

        UserDto userDto = userTransformer.transform(userRepository.findByUsername(username));
        if(userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }

        model.addAttribute("firstName", userDto.getFirstName());

        return "tasks";
    }
}