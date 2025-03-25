package com.petproject.tasks.controller;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.service.TaskService;
import com.petproject.tasks.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public String showCalendarPage(@PathVariable("userId") Long userId, Model model) {
        UserDto userDto = userService.getUserById(userId);
        model.addAttribute("firstName", userDto.getFirstName());
        model.addAttribute("userId", userDto.getId());
        List<Map<String, String>> events = taskService.getEvents(userId);

        model.addAttribute("events", events);
        return "calendar";
    }

    @GetMapping("/{userId}/{date}")
    public String showTasksListByDate(@PathVariable("userId") Long userId,
                                      @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      Model model) {
        List<TaskDto> tasks = taskService.getTasksByUserIdAndDate(userId, date);
        model.addAttribute("tasks", tasks);
        model.addAttribute("taskDto", new TaskDto());
        return "dateTasks";
    }

    @PostMapping("/{userId}/{date}")
    public String addNewTask(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @PathVariable("userId") Long userId, @Valid @ModelAttribute("taskDto") TaskDto taskDto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("tasks", taskService.getTasksByUserIdAndDate(userId, date));
            model.addAttribute("hasErrors", true);
            return "dateTasks";
        }
        taskService.saveTaskByUserIdAndDate(taskDto, userId, date);
        return "redirect:/tasks/" + userId;
    }

    @PostMapping("/update/{taskId}")
    public String updateTask(@PathVariable("taskId") Long taskId,
                             @RequestParam("userId") Long userId,
                             @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @Valid @ModelAttribute("taskDto") TaskDto taskDto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct any errors in the form");
            model.addAttribute("tasks", taskService.getTasksByUserIdAndDate(userId, date));
            model.addAttribute("taskDto", taskDto);
            model.addAttribute("userId", userId);
            model.addAttribute("hasErrors", true);
            return "dateTasks";
        }
        taskService.updateTask(taskId, taskDto);
        return "redirect:/tasks/" + userId;
    }

    @PostMapping("/delete/{taskId}")
    public String deleteTask(@RequestParam("userId") Long userId, @PathVariable("taskId") Long taskId) {
        taskService.deleteTaskById(taskId);
        return "redirect:/tasks/" + userId;
    }

    @PostMapping("/complete/{taskId}")
    public String completeTask(@RequestParam("userId") Long userId, @PathVariable("taskId") Long taskId) {
        taskService.changeTaskStatus(taskId, TaskStatus.DONE);
        return "redirect:/tasks/" + userId;
    }

    @PostMapping("/cancel/{taskId}")
    public String cancelTask(@RequestParam("userId") Long userId, @PathVariable("taskId") Long taskId) {
        taskService.changeTaskStatus(taskId, TaskStatus.CANCELED);
        return "redirect:/tasks/" + userId;
    }

    @GetMapping("/{userId}/search")
    public String searchTasks(@PathVariable("userId") Long userId,
                              @RequestParam(value = "query", required = false) String keyword, Model model) {
        if (keyword != null && keyword.isBlank()) {
            model.addAttribute("blankField", true);
            return "search";
        }
        List<TaskDto> searchResult = taskService.searchTasks(userId, keyword);
        model.addAttribute("tasks", searchResult);
        model.addAttribute("searchPerformed", true);
        return "search";
    }
}