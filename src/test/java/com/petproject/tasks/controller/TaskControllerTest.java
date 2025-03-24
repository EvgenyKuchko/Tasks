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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
    private Long taskId;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .firstName("Zia")
                .username("user123")
                .password("pass123")
                .roles(Collections.singleton(UserRole.USER))
                .build();

        taskId = 8L;
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
    }

    //тест создания и измменения задачи

    @Test
    public void shouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTaskById(taskId);

        this.mockMvc.perform(post("/tasks/delete/" + taskId)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("userId", String.valueOf(userDto.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));
    }

    @Test
    public void shouldCompleteTask() throws Exception {
        doNothing().when(taskService).changeTaskStatus(taskId, TaskStatus.DONE);

        this.mockMvc.perform(post("/tasks/complete/" + taskId)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("userId", String.valueOf(userDto.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));
    }

    @Test
    public void shouldCancelTask() throws Exception {
        doNothing().when(taskService).changeTaskStatus(taskId, TaskStatus.CANCELED);

        this.mockMvc.perform(post("/tasks/cancel/" + taskId)
                        .with(csrf())
                        .with(user(userDto.getUsername()).password(userDto.getPassword()).roles(String.valueOf(userDto.getRoles())))
                        .param("userId", String.valueOf(userDto.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));
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
    }
}