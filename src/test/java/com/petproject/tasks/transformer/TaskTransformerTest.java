package com.petproject.tasks.transformer;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TaskTransformerTest {

    @Autowired
    private TaskTransformer taskTransformer;

    private final String TITLE = "work";
    private final String DESCRIPTION = "go to work";
    private final LocalDate DATE = LocalDate.parse("2025-05-07");

    @Test
    public void shouldTransformTaskToTaskDto() {
        User user = User.builder()
                .firstName("Ali")
                .username("aliali")
                .password("password")
                .build();

        Task task = Task.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .date(DATE)
                .status(TaskStatus.DONE)
                .user(user)
                .build();

        TaskDto resultingTask = taskTransformer.transform(task);

        assertThat(resultingTask).isNotNull();
        assertThat(resultingTask.getTitle()).isEqualTo(task.getTitle());
        assertThat(resultingTask.getDescription()).isEqualTo(task.getDescription());
        assertThat(resultingTask.getDate()).isEqualTo(task.getDate());
        assertThat(resultingTask.getStatus()).isEqualTo(task.getStatus());
    }

    @Test
    public void shouldTransformTaskDtoToTask() {
        TaskDto taskDto = TaskDto.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .date(DATE)
                .status(TaskStatus.ACTIVE)
                .build();

        Task resultingTask = taskTransformer.transform(taskDto);

        assertThat(resultingTask).isNotNull();
        assertThat(resultingTask.getTitle()).isEqualTo(taskDto.getTitle());
        assertThat(resultingTask.getDescription()).isEqualTo(taskDto.getDescription());
        assertThat(resultingTask.getDate()).isEqualTo(taskDto.getDate());
        assertThat(resultingTask.getStatus()).isEqualTo(taskDto.getStatus());
    }
}