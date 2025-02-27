package com.petproject.tasks.transformer;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskTransformer implements Transformer<Task, TaskDto>{
    @Override
    public TaskDto transform(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .date(task.getDate())
                .username(task.getUser().getUsername())
                .build();
    }

    @Override
    public Task transform(TaskDto taskDto) {
        return Task.builder()
                .id(taskDto.getId())
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .date(taskDto.getDate())
                .build();
    }
}
