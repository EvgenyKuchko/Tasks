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
import java.util.*;

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
    private final LocalDate DATE = LocalDate.parse("2025-05-07");
    private final String KEYWORD = "meeting";

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("Ali")
                .username("aliali")
                .password("password")
                .build();

        taskDto = TaskDto.builder()
                .title("work")
                .description("go to work")
                .date(DATE)
                .status(TaskStatus.DONE)
                .username(user.getUsername())
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

        verify(taskRepository, times(1)).getTasksByUserId(USER_ID);
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTask.size()).isEqualTo(tasks.size());
    }

    @Test
    public void shouldReturnTaskDtoByUserIdAndDate() {
        when(taskRepository.getTasksByUserIdAndDate(USER_ID, DATE)).thenReturn(tasks);

        List<TaskDto> resultingTasks = taskService.getTasksByUserIdAndDate(USER_ID, DATE);

        verify(taskRepository, times(1)).getTasksByUserIdAndDate(USER_ID, DATE);
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTasks.size()).isEqualTo(tasks.size());
    }

    @Test
    public void shouldSaveTaskByUserIdAndDate() {
        when(taskTransformer.transform(taskDto)).thenReturn(task);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(user);

        taskService.saveTaskByUserIdAndDate(taskDto, USER_ID, DATE);

        verify(taskTransformer, times(1)).transform(taskDto);
        verify(userRepository, times(1)).getReferenceById(USER_ID);
        verify(taskRepository, times(1)).save(task);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(task.getDate()).isEqualTo(DATE);
        assertThat(task.getUser()).isEqualTo(user);
    }

    @Test
    public void shouldUpdateTaskWithoutErrors() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.ofNullable(task));

        taskService.updateTask(TASK_ID, taskDto);

        verify(taskRepository, times(1)).findById(TASK_ID);
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
        when(taskRepository.searchTasks(USER_ID, KEYWORD)).thenReturn(tasks);

        List<TaskDto> resultingTasks = taskService.searchTasks(USER_ID, KEYWORD);

        verify(taskRepository, times(1)).searchTasks(USER_ID, KEYWORD);
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTasks).isNotNull();
        assertThat(resultingTasks.size()).isEqualTo(tasks.size());
    }

    @Test
    public void shouldReturnEmptyList() {
        when(taskRepository.searchTasks(USER_ID, KEYWORD)).thenReturn(Collections.emptyList());

        List<TaskDto> resultingTasks = taskService.searchTasks(USER_ID, KEYWORD);

        verify(taskRepository, times(1)).searchTasks(USER_ID, KEYWORD);
        verifyNoMoreInteractions(taskRepository);
        assertThat(resultingTasks).isNotNull();
        assertThat(resultingTasks.isEmpty()).isEqualTo(true);
    }

    @Test
    public void shouldNewTask() {
        when(taskTransformer.transform(taskDto)).thenReturn(task);
        when(userRepository.findByUsername(taskDto.getUsername())).thenReturn(user);

        taskService.saveNewTask(taskDto);

        verify(taskTransformer, times(1)).transform(taskDto);
        verify(userRepository, times(1)).findByUsername(taskDto.getUsername());
        verify(taskRepository, times(1)).save(task);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(task.getUser()).isEqualTo(user);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void shouldReturnActiveTasksForCalendar() {
        Long userId = 1L;
        LocalDate dateOne = LocalDate.of(2025, 3, 13);
        LocalDate dateTwo = LocalDate.of(2025, 3, 14);

        Task task1 = new Task(1L, "Task 1", "Desc 1", dateOne, TaskStatus.ACTIVE, user);
        Task task2 = new Task(2L, "Task 2", "Desc 2", dateOne, TaskStatus.ACTIVE, user);
        Task task3 = new Task(3L, "Task 3", "Desc 3", dateOne, TaskStatus.DONE, user);
        Task task4 = new Task(4L, "Task 4", "Desc 4", dateTwo, TaskStatus.ACTIVE, user);

        List<Task> mockTasks = Arrays.asList(task1, task2, task3, task4);

        TaskDto taskDto1 = new TaskDto(1L, "Task 1", "Desc 1", dateOne, TaskStatus.ACTIVE, user.getUsername());
        TaskDto taskDto2 = new TaskDto(2L, "Task 2", "Desc 2", dateOne, TaskStatus.ACTIVE, user.getUsername());
        TaskDto taskDto3 = new TaskDto(3L, "Task 3", "Desc 3", dateOne, TaskStatus.DONE, user.getUsername());
        TaskDto taskDto4 = new TaskDto(4L, "Task 4", "Desc 4", dateTwo, TaskStatus.ACTIVE, user.getUsername());

        when(taskRepository.getTasksByUserId(userId)).thenReturn(mockTasks);
        when(taskTransformer.transform(task1)).thenReturn(taskDto1);
        when(taskTransformer.transform(task2)).thenReturn(taskDto2);
        when(taskTransformer.transform(task3)).thenReturn(taskDto3);
        when(taskTransformer.transform(task4)).thenReturn(taskDto4);

        List<Map<String, String>> events = taskService.getEvents(userId);

        assertThat(events.size()).isEqualTo(2);

        assertThat(events.get(0).get("title")).isEqualTo("🔹 1 задачи");
        assertThat(events.get(0).get("start")).isEqualTo(dateTwo.toString());

        assertThat(events.get(1).get("title")).isEqualTo("🔹 2 задачи");
        assertThat(events.get(1).get("start")).isEqualTo(dateOne.toString());

        verify(taskRepository, times(1)).getTasksByUserId(userId);
        verify(taskTransformer, times(4)).transform(any(Task.class));
    }

    @Test
    public void shouldReturnTasksFilteredByKeywordAndDate() {
        User user1 = User.builder()
                .username("kevin11")
                .build();

        LocalDate dateOne = LocalDate.of(2025, 3, 13);
        LocalDate dateTwo = LocalDate.of(2025, 3, 14);

        Task task1 = new Task(1L, "sport", "swimming", dateOne, TaskStatus.ACTIVE, user);
        Task task2 = new Task(2L, "work", "project", dateOne, TaskStatus.ACTIVE, user);
        Task task3 = new Task(3L, "sport", "tennis", dateOne, TaskStatus.DONE, user1);
        Task task4 = new Task(4L, "relax", "spa", dateTwo, TaskStatus.ACTIVE, user);

        List<Task> filteredTasks = Arrays.asList(task1, task2, task3, task4);

        TaskDto taskDto1 = new TaskDto(1L, "sport", "swimming", dateOne, TaskStatus.ACTIVE, user.getUsername());
        TaskDto taskDto2 = new TaskDto(2L, "work", "project", dateOne, TaskStatus.ACTIVE, user.getUsername());
        TaskDto taskDto3 = new TaskDto(3L, "sport", "tennis", dateOne, TaskStatus.DONE, user1.getUsername());
        TaskDto taskDto4 = new TaskDto(4L, "relax", "spa", dateTwo, TaskStatus.ACTIVE, user.getUsername());

        when(taskRepository.findAll()).thenReturn(filteredTasks);
        when(taskTransformer.transform(task1)).thenReturn(taskDto1);
        when(taskTransformer.transform(task2)).thenReturn(taskDto2);
        when(taskTransformer.transform(task3)).thenReturn(taskDto3);
        when(taskTransformer.transform(task4)).thenReturn(taskDto4);

        String keyword = "sport";

        List<TaskDto> resultingTasks = taskService.getFilteredTasks(keyword, null, dateOne);

        assertThat(resultingTasks.size()).isEqualTo(2);
        assertThat(resultingTasks.contains(taskDto1)).isEqualTo(true);
        assertThat(resultingTasks.contains(taskDto3)).isEqualTo(true);

        verify(taskRepository, times(1)).findAll();
        verify(taskTransformer, times(4)).transform(any(Task.class));
    }
}