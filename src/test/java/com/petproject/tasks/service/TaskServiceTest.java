package com.petproject.tasks.service;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.repository.TaskRepository;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.transformer.TaskTransformer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskTransformer taskTransformer;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    private TaskDto taskDto;
    private List<Task> tasks;
    private List<TaskDto> dtoTasks;
    private User user;
    private Task task;

    private final Long USER_ID = 1L;
    private final Long TASK_ID = 999L;
    private final String TITLE = "work";
    private final String DESCRIPTION = "go to work";
    private final LocalDate DATE = LocalDate.parse("2025-05-07");

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("Ali")
                .username("aliali")
                .password("password")
                .build();

        taskDto = TaskDto.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .date(DATE)
                .status(TaskStatus.DONE)
                .build();

        dtoTasks = new ArrayList<>();
        dtoTasks.add(taskDto);

        task = new Task();

        tasks = new ArrayList<>();
        tasks.add(task);
    }

    @Test
    public void shouldReturnTaskDtoByUserId() {
        when(taskRepository.getTasksByUserId(USER_ID)).thenReturn(tasks);

        List<TaskDto> resultingTask = taskService.getTasksByUserId(USER_ID);

        verify(taskRepository).getTasksByUserId(USER_ID);
        verify(taskRepository, times(1)).getTasksByUserId(USER_ID);
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTask.size()).isEqualTo(tasks.size());
    }

    @Test
    public void shouldReturnTaskDtoByUserIdAndDate() {
        when(taskRepository.getTasksByUserIdAndDate(USER_ID, DATE)).thenReturn(tasks);

        List<TaskDto> resultingTasks = taskService.getTasksByUserIdAndDate(USER_ID, DATE);

        verify(taskRepository).getTasksByUserIdAndDate(USER_ID, DATE);
        verify(taskRepository, times(1)).getTasksByUserIdAndDate(USER_ID, DATE);
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTasks.size()).isEqualTo(tasks.size());
    }

    @Test
    public void shouldSaveTaskByUserIdAndDate() {
        when(taskTransformer.transform(taskDto)).thenReturn(task);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(user);

        taskService.saveTaskByUserIdAndDate(taskDto, USER_ID, DATE);

        verify(taskTransformer).transform(taskDto);
        verify(taskTransformer, times(1)).transform(taskDto);
        verify(userRepository).getReferenceById(USER_ID);
        verify(userRepository, times(1)).getReferenceById(USER_ID);
        verify(taskRepository).save(task);
        verify(taskRepository, times(1)).save(task);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(task.getDate()).isEqualTo(DATE);
        assertThat(task.getUser()).isEqualTo(user);
    }

    @Test
    public void shouldUpdateTaskWithoutErrors() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.ofNullable(task));

        taskService.updateTask(TASK_ID, taskDto);

        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskRepository).save(task);
        verify(taskRepository, times(1)).save(task);

        assertThat(task.getTitle()).isEqualTo(taskDto.getTitle());
        assertThat(task.getDescription()).isEqualTo(taskDto.getDescription());
        assertThat(task.getDate()).isEqualTo(taskDto.getDate());
    }

    @Test
    public void shouldFailUpdateTaskAndThrowEntityNotFoundException() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(TASK_ID, taskDto));

        assertThat("Task with ID " + TASK_ID + " is not found").isEqualTo(exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void shouldDeleteTaskById() {
        taskService.deleteTaskById(TASK_ID);

        verify(taskRepository, times(1)).deleteById(TASK_ID);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void shouldChangeTaskStatus() {
        taskService.changeTaskStatus(TASK_ID, TaskStatus.CANCELED);

        verify(taskRepository, times(1)).changeTaskStatus(TASK_ID, TaskStatus.CANCELED);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void ShouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskDto> resultingTasks = taskService.findAllTasks();

        verify(taskRepository, times(1)).findAll();
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTasks).isNotNull();
        assertThat(resultingTasks.size()).isEqualTo(tasks.size());
    }

    @Test
    public void shouldReturnTasksByKeyword() {
        String keyword = "meeting";

        when(taskRepository.searchTasks(USER_ID, keyword)).thenReturn(tasks);

        List<TaskDto> resultingTasks = taskService.searchTasks(USER_ID, keyword);

        verify(taskRepository, times(1)).searchTasks(USER_ID, keyword);
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTasks).isNotNull();
        assertThat(resultingTasks.size()).isEqualTo(tasks.size());
    }

    @Test
    public void shouldNewTask() {
        String username = "aliali";
        taskDto.setUsername(username);

        when(taskTransformer.transform(taskDto)).thenReturn(task);
        when(userRepository.findByUsername(username)).thenReturn(user);

        taskService.saveNewTask(taskDto);

        verify(taskTransformer, times(1)).transform(taskDto);
        verify(userRepository, times(1)).findByUsername(username);
        verify(taskRepository, times(1)).save(task);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(task.getUser()).isEqualTo(user);
        verifyNoMoreInteractions(taskRepository);
    }
}