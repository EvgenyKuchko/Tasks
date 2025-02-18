package com.petproject.tasks.service;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.entity.Task;
import com.petproject.tasks.repository.TaskRepository;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.transformer.TaskTransformer;
import com.petproject.tasks.transformer.UserTransformer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskTransformer taskTransformer;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTransformer userTransformer;

    @Transactional
    public List<TaskDto> getTasksByUserId(Long userId) {
        return taskRepository.getTasksByUserId(userId).stream()
                .map(x -> taskTransformer.transform(x))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveTask(TaskDto taskDto, Long userId) {
        Task task = taskTransformer.transform(taskDto);
 //       task.setUser(userTransformer.transform(userService.getUserById(userId)));
        task.setUser(userRepository.getReferenceById(userId));
        taskRepository.save(task);
    }
}