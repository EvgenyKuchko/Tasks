package com.petproject.tasks.service;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.repository.TaskRepository;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.transformer.TaskTransformer;
import com.petproject.tasks.transformer.UserTransformer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    public void saveTaskByUserIdAndDate(TaskDto taskDto, Long userId, LocalDate date) {
        taskDto.setCreationDate(date);
        taskDto.setStatus(TaskStatus.ACTIVE);
        Task task = taskTransformer.transform(taskDto);
        task.setUser(userRepository.getReferenceById(userId));
        taskRepository.save(task);
    }

    @Transactional
    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + taskId + " is not found"));
    }

    @Transactional
    public void updateTask(Long taskId, TaskDto taskDto) {
        Task task = findTaskById(taskId);
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setCreationDate(taskDto.getCreationDate());
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public void changeTaskStatusToDone(Long taskId) {
        taskRepository.changeTaskStatusToDone(taskId, TaskStatus.DONE);
    }
}