package com.petproject.tasks.controller;

import com.petproject.tasks.config.SecurityConfig;
import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.TaskService;
import com.petproject.tasks.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TaskService taskService;

    private UserDto userDto;
    private TaskDto taskDto;
    private final Long TASK_ID = 8L;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .firstName("Zia")
                .username("user123")
                .password("pass123")
                .roles(Collections.singleton(UserRole.USER))
                .build();

        taskDto = TaskDto.builder()
                .id(TASK_ID)
                .title("title")
                .description("description")
                .username(userDto.getUsername())
                .status(TaskStatus.ACTIVE)
                .date(LocalDate.parse("2025-05-07"))
                .build();
    }

    @Test
    public void shouldReturnCalendarPage() throws Exception {
        List<Map<String, String>> events = List.of(
                Map.of("title", "🔹 2 задачи", "start", "2025-03-21"),
                Map.of("title", "🔹 1 задача", "start", "2025-03-22")
        );

        when(taskService.getEvents(userDto.getId())).thenReturn(events);
        when(userService.getUserById(userDto.getId())).thenReturn(userDto);

        this.mockMvc.perform(get("/tasks/" + userDto.getId())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles()))))
                .andExpect(status().isOk())
                .andExpect(view().name("calendar"))
                .andExpect(model().attributeExists("firstName", "userId", "events"));

        verify(userService, times(1)).getUserById(any());
        verify(taskService, times(1)).getEvents(any());
    }

    @Test
    public void shouldReturnTasksListByDate() throws Exception {
        LocalDate taskDate = LocalDate.of(2025, 3, 21);

        List<TaskDto> tasks = new ArrayList<>(List.of(
                new TaskDto(1L, "cinema", "go to the cinema at 18:00", taskDate, TaskStatus.ACTIVE, userDto.getFirstName()),
                new TaskDto(2L, "shopping", "buy the closes", taskDate, TaskStatus.DONE, userDto.getFirstName())));

        when(taskService.getTasksByUserIdAndDate(userDto.getId(), taskDate)).thenReturn(tasks);

        this.mockMvc.perform(get("/tasks/" + userDto.getId() + "/" + taskDate)
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles()))))
                .andExpect(status().isOk())
                .andExpect(view().name("dateTasks"))
                .andExpect(model().attributeExists("tasks"));

        verify(taskService, times(1)).getTasksByUserIdAndDate(any(), any());
    }

    @Test
    public void shouldFailAddNewTaskWithValidationErrorAndReturnSamePage() throws Exception {
        this.mockMvc.perform(post("/tasks/" + userDto.getId() + "/" + taskDto.getDate())
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("title", taskDto.getTitle())
                        .param("description", "  ")
                        .param("date", String.valueOf(taskDto.getDate())))
                .andExpect(status().isOk())
                .andExpect(view().name("dateTasks"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("hasErrors"));

        verify(taskService, times(0)).saveTaskByUserIdAndDate(any(), anyLong(), any());
    }

    @Test
    public void shouldSuccessCreateNewTaskAndRedirect() throws Exception {
        doNothing().when(taskService).saveTaskByUserIdAndDate(taskDto, userDto.getId(), taskDto.getDate());

        this.mockMvc.perform(post("/tasks/" + userDto.getId() + "/" + taskDto.getDate())
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("title", taskDto.getTitle())
                        .param("description", taskDto.getDescription())
                        .param("date", String.valueOf(taskDto.getDate()))
                        .param("username", userDto.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));

        verify(taskService, times(1)).saveTaskByUserIdAndDate(any(TaskDto.class), eq(1L), any(LocalDate.class));
    }

    @Test
    public void shouldFailUpdateTaskWithValidationErrorAndReturnSamePage() throws Exception {
        this.mockMvc.perform(post("/tasks/update/" + TASK_ID)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("title", taskDto.getTitle())
                        .param("description", "  ")
                        .param("date", String.valueOf(taskDto.getDate()))
                        .param("userId", String.valueOf(userDto.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("dateTasks"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("hasErrors"));

        verify(taskService, times(0)).updateTask(any(), any());
    }

    @Test
    public void shouldSuccessUpdateTaskWithValidationErrorAndRedirect() throws Exception {
        doNothing().when(taskService).updateTask(TASK_ID, taskDto);

        this.mockMvc.perform(post("/tasks/update/" + TASK_ID)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("title", taskDto.getTitle())
                        .param("description", taskDto.getDescription())
                        .param("date", String.valueOf(taskDto.getDate()))
                        .param("userId", String.valueOf(userDto.getId()))
                        .param("username", userDto.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));

        verify(taskService, times(1)).updateTask(any(), any());
    }

    @Test
    public void shouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTaskById(TASK_ID);

        this.mockMvc.perform(post("/tasks/delete/" + TASK_ID)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("userId", String.valueOf(userDto.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));

        verify(taskService, times(1)).deleteTaskById(any());
    }

    @Test
    public void shouldCompleteTask() throws Exception {
        doNothing().when(taskService).changeTaskStatus(TASK_ID, TaskStatus.DONE);

        this.mockMvc.perform(post("/tasks/complete/" + TASK_ID)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("userId", String.valueOf(userDto.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));

        verify(taskService, times(1)).changeTaskStatus(any(), any());
    }

    @Test
    public void shouldCancelTask() throws Exception {
        doNothing().when(taskService).changeTaskStatus(TASK_ID, TaskStatus.CANCELED);

        this.mockMvc.perform(post("/tasks/cancel/" + TASK_ID)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("userId", String.valueOf(userDto.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));

        verify(taskService, times(1)).changeTaskStatus(any(), any());
    }

    @Test
    public void shouldSearchTasksWithBlankKeyword() throws Exception {
        this.mockMvc.perform(get("/tasks/" + userDto.getId() + "/search")
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("query", "  "))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("blankField"));

        verify(taskService, times(0)).searchTasks(any(), any());
    }

    @Test
    public void shouldSearchTasksWithKeywordAndReturnEmptyTasksList() throws Exception {
        String keyword = "training";
        List<TaskDto> tasks = Collections.emptyList();

        when(taskService.searchTasks(userDto.getId(), keyword)).thenReturn(tasks);

        this.mockMvc.perform(get("/tasks/" + userDto.getId() + "/search")
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("query", keyword))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("searchPerformed"))
                .andExpect(model().attributeExists("tasks"));

        verify(taskService, times(1)).searchTasks(any(), any());
    }
}