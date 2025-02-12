package com.petproject.tasks.service;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.entity.Task;
import com.petproject.tasks.repository.TaskRepository;
import com.petproject.tasks.transformer.TaskTransformer;
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

    @Transactional
    public List<TaskDto> getTasksByUserId(Long userId) {
        return taskRepository.getTasksByUserId(userId).stream()
                .map(x -> taskTransformer.transform(x))
                .collect(Collectors.toList());
    }
}