package com.petproject.tasks.controller;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.repository.TaskRepository;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.service.TaskService;
import com.petproject.tasks.transformer.TaskTransformer;
import com.petproject.tasks.transformer.UserTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("tasks")
public class TaskController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTransformer userTransformer;
    @Autowired
    private TaskService taskService;

    @GetMapping
    public String showCalendarPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();

        UserDto userDto = userTransformer.transform(userRepository.findByUsername(username));
        if(userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }
        model.addAttribute("firstName", userDto.getFirstName());

        List<Map<String, String>> events = new ArrayList<>();

//        for (TaskDto task : taskService.getTasksByUserId(userDto.getId())) {  // Загружаем все задачи из БД
//            Map<String, Object> event = new HashMap<>();
//            event.put("title", task.getTitle());
//            event.put("creation date", task.getCreationDate().toString());  // Дата выполнения
//            event.put("description", task.getDescription());  // Описание
//            event.put("status", task.getStatus().name());  // Статус задачи
//            event.put("id", task.getId());  // ID задачи (чтобы потом к ней обращаться)
//            events.add(event);
//        }

        List<TaskDto> tasks = taskService.getTasksByUserId(userDto.getId());
        Map<LocalDate, List<TaskDto>> TASKS = new HashMap<>();

        for (TaskDto task : tasks) {
            TASKS.computeIfAbsent(task.getCreationDate(), k -> new ArrayList<>()).add(task);
        }

        for (Map.Entry<LocalDate, List<TaskDto>> entry : TASKS.entrySet()) {
            Map<String, String> event = new HashMap<>();
            event.put("title", "🔹 " + entry.getValue().size() + " задачи");
            event.put("start", entry.getKey().toString());
            events.add(event);
        }

        model.addAttribute("events", events);

        return "calendar";
    }
}