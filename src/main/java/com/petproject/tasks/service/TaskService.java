package com.petproject.tasks.service;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.repository.TaskRepository;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.transformer.TaskTransformer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskTransformer taskTransformer;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<TaskDto> getTasksByUserId(Long userId) {
        return taskRepository.getTasksByUserId(userId).stream()
                .map(x -> taskTransformer.transform(x))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TaskDto> getTasksByUserIdAndDate(Long userId, LocalDate localDate) {
        return taskRepository.getTasksByUserIdAndDate(userId, localDate).stream()
                .map(x -> taskTransformer.transform(x))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveTaskByUserIdAndDate(TaskDto taskDto, Long userId, LocalDate date) {
        Task task = taskTransformer.transform(taskDto);
        task.setDate(date);
        task.setStatus(TaskStatus.ACTIVE);
        task.setUser(userRepository.getReferenceById(userId));
        taskRepository.save(task);
    }

    @Transactional
    public List<Map<String, String>> getEvents(Long userId) {
        List<Map<String, String>> events = new ArrayList<>();

        List<TaskDto> tasks = getTasksByUserId(userId).stream()
                .filter(taskDto -> taskDto.getStatus().equals(TaskStatus.ACTIVE))
                .toList();
        Map<LocalDate, List<TaskDto>> TASKS = new HashMap<>();

        for (TaskDto task : tasks) {
            TASKS.computeIfAbsent(task.getDate(), k -> new ArrayList<>()).add(task);
        }

        for (Map.Entry<LocalDate, List<TaskDto>> entry : TASKS.entrySet()) {
            Map<String, String> event = new HashMap<>();
            event.put("title", "🔹 " + entry.getValue().size() + " задачи");
            event.put("start", entry.getKey().toString());
            events.add(event);
        }
        return events;
    }

    @Transactional
    public void updateTask(Long taskId, TaskDto taskDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + taskId + " is not found"));
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDate(taskDto.getDate());
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public void changeTaskStatus(Long taskId, TaskStatus taskStatus) {
        taskRepository.changeTaskStatus(taskId, taskStatus);
    }

    @Transactional
    public List<TaskDto> searchTasks(Long userId, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
             return taskRepository.searchTasks(userId, keyword).stream()
                    .map(x -> taskTransformer.transform(x))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<TaskDto> findAllTasks() {
        return taskRepository.findAll().stream()
                .map(x -> taskTransformer.transform(x)).sorted().collect(Collectors.toList());
    }

    @Transactional
    public void saveNewTask(TaskDto taskDto) {
        Task task = taskTransformer.transform(taskDto);
        task.setStatus(TaskStatus.ACTIVE);
        task.setUser(userRepository.findByUsername(taskDto.getUsername()));
        taskRepository.save(task);
    }

    @Transactional
    public List<TaskDto> getFilteredTasks(String keyword, String username, LocalDate date) {
        List<TaskDto> tasks = taskRepository.findAll().stream()
                .map(x -> taskTransformer.transform(x)).sorted().collect(Collectors.toList());

        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            tasks = tasks.stream()
                    .filter(taskDto -> taskDto.getTitle().toLowerCase().contains(lowerKeyword) ||
                            taskDto.getDescription().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }

        if (username != null && !username.isEmpty()) {
            tasks = tasks.stream()
                    .filter(taskDto -> taskDto.getUsername().equals(username))
                    .collect(Collectors.toList());
        }

        if (date != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getDate().equals(date))
                    .collect(Collectors.toList());
        }
        return tasks;
    }
}