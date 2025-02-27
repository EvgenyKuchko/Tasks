package com.petproject.tasks.controller;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminTasksController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/tasks")
    public String showAdminTasksPage(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "date", required = false) LocalDate date) {
        List<TaskDto> tasks = taskService.findAllTasks();
        Set<String> usernames = tasks.stream()
                .map(TaskDto::getUsername)
                .collect(Collectors.toSet());

        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            tasks = tasks.stream()
                    .filter(taskDto -> taskDto.getTitle().toLowerCase().contains(lowerKeyword) ||
                            taskDto.getDescription().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }

        if(username != null && !username.isEmpty()) {
            tasks = tasks.stream()
                    .filter(taskDto -> taskDto.getUsername().equals(username))
                    .collect(Collectors.toList());
        }

        if (date != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getDate().equals(date))
                    .collect(Collectors.toList());
        }

        TaskDto taskDto = new TaskDto();
        model.addAttribute("usernames", usernames);
        model.addAttribute("task", taskDto);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
}