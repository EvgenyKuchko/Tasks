package com.petproject.tasks.controller;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminTasksController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/tasks")
    public String showAdminTasksPage(Model model) {
        List<TaskDto> tasks = taskService.findAllTasks();
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
}