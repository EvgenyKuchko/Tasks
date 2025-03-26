package com.petproject.tasks.controller;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.service.TaskService;
import com.petproject.tasks.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
        List<TaskDto> tasks = taskService.getFilteredTasks(keyword, username, date);
        Set<String> usernames = userService.getAllUsers().stream()
                .map(UserDto::getUsername)
                .collect(Collectors.toSet());

        model.addAttribute("usernames", usernames);
        model.addAttribute("taskDto", new TaskDto());
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @PostMapping("/tasks/{taskId}/update")
    public String updateTask(@PathVariable Long taskId,
                             @Valid @ModelAttribute("taskDto") TaskDto taskDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("tasks", taskService.findAllTasks());
            model.addAttribute("taskDto", taskDto);
            model.addAttribute("updateTaskId", taskId);
            return "tasks";
        }

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
    public String createNewTask(@Valid @ModelAttribute("taskDto") TaskDto taskDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("tasks", taskService.findAllTasks());
            model.addAttribute("usernames", userService.getAllUsers().stream()
                    .map(UserDto::getUsername)
                    .collect(Collectors.toSet()));
            model.addAttribute("taskDto", taskDto);
            model.addAttribute("hasErrors", true);
            return "tasks";
        }
        taskService.saveNewTask(taskDto);
        return "redirect:/admin/tasks";
    }
}