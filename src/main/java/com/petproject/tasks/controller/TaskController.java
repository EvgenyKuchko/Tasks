package com.petproject.tasks.controller;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.service.TaskService;
import com.petproject.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
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
        return "dateTasks";
    }

    @PostMapping("/{userId}/{date}")
    public String addNewTask(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @PathVariable("userId") Long userId, @ModelAttribute TaskDto taskDto) {
        taskService.saveTaskByUserIdAndDate(taskDto, userId, date);
        return "redirect:/tasks/" + userId;
    }

    @PostMapping("/update/{taskId}")
    public String updateTask(@PathVariable("taskId") Long taskId, @ModelAttribute TaskDto taskDto,
                             @RequestParam("userId") Long userId) {
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
        List<TaskDto> searchResult = (keyword != null && !keyword.isEmpty()) ? taskService.searchTasks(userId, keyword) : Collections.emptyList();
        model.addAttribute("tasks", searchResult);
        model.addAttribute("searchPerformed", true);
        return "search";
    }

}