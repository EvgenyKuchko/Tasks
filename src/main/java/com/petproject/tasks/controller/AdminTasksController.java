package com.petproject.tasks.controller;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.service.TaskService;
import com.petproject.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminTasksController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/tasks")
    public String showAdminTasksPage(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "date", required = false) LocalDate date) {
        List<TaskDto> tasks = taskService.findAllTasks();
        Set<String> usernames = userService.getAllUsers().stream()
                .map(UserDto::getUsername)
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

        model.addAttribute("usernames", usernames);
        model.addAttribute("task", new TaskDto());
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @PostMapping("/tasks/{taskId}/update")
    public String updateTask(@ModelAttribute TaskDto taskDto,
                             @PathVariable("taskId") Long taskId) {
        taskService.updateTask(taskId, taskDto);
        return "redirect:/admin/tasks";
    }

    @PostMapping("/tasks/{taskId}/complete")
    public String completeTask(@PathVariable("taskId") Long taskId) {
        taskService.changeTaskStatus(taskId, TaskStatus.DONE);
        return "redirect:/admin/tasks";
    }

    @PostMapping("/tasks/{taskId}/cancel")
    public String cancelTask(@PathVariable("taskId") Long taskId) {
        taskService.changeTaskStatus(taskId, TaskStatus.CANCELED);
        return "redirect:/admin/tasks";
    }

    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable("taskId") Long taskId) {
        taskService.deleteTaskById(taskId);
        return "redirect:/admin/tasks";
    }

    @PostMapping("/tasks/create")
    public String createNewTask(@ModelAttribute TaskDto taskDto) {
        taskService.saveNewTask(taskDto);
        return "redirect:/admin/tasks";
    }
}