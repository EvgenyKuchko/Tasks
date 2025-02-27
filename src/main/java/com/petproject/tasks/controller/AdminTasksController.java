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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminTasksController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/tasks")
    public String showAdminTasksPage(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "userId", required = false) Long userId,
                                     @RequestParam(value = "date", required = false) LocalDate date) {
        List<TaskDto> tasks = taskService.findAllTasks();

        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            tasks = tasks.stream()
                    .filter(taskDto -> taskDto.getTitle().toLowerCase().contains(lowerKeyword) ||
                            taskDto.getDescription().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }

        TaskDto taskDto = new TaskDto();
        model.addAttribute("task", taskDto);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
}