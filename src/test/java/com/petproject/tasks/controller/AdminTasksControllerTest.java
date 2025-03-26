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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminTasksController.class)
@Import(SecurityConfig.class)
public class AdminTasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TaskService taskService;

    private UserDto userDto;
    private TaskDto taskDto;
    private List<TaskDto> tasks;
    private List<UserDto> users;

    private final Long TASK_ID = 1L;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(99L)
                .firstName("Mike")
                .username("admin123")
                .password("adm123")
                .roles(Set.of(UserRole.USER, UserRole.ADMIN))
                .build();

        taskDto = TaskDto.builder()
                .id(TASK_ID)
                .title("Running")
                .description("3 km distance")
                .date(LocalDate.parse("2025-05-07"))
                .username(userDto.getUsername())
                .status(TaskStatus.ACTIVE)
                .build();

        tasks = new ArrayList<>();
        tasks.add(taskDto);
        users = new ArrayList<>();
        users.add(userDto);
    }

    @Test
    public void testShowAdminTasksPage_Success_ReturnPage() throws Exception {
        String keyword = "keyword";
        String username = "username";
        LocalDate date = LocalDate.now();

        when(taskService.getFilteredTasks(keyword, username, date)).thenReturn(tasks);
        when(userService.getAllUsers()).thenReturn(users);

        this.mockMvc.perform(get("/admin/tasks")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("keyword", keyword)
                        .param("username", username)
                        .param("date", String.valueOf(date)))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attributeExists("usernames"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("taskDto"));

        verify(taskService, times(1)).getFilteredTasks(any(), any(), any());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testUpdateTask_Success_RedirectToTasksPage() throws Exception {
        doNothing().when(taskService).updateTask(TASK_ID, taskDto);

        this.mockMvc.perform(post("/admin/tasks/" + TASK_ID + "/update")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("title", taskDto.getTitle())
                        .param("description", taskDto.getDescription())
                        .param("date", String.valueOf(LocalDate.now()))
                        .param("username", userDto.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tasks"));

        verify(taskService, times(1)).updateTask(any(), any());
    }

    @Test
    public void testUpdateTask_FailWithValidationErrors_ReturnFormWithErrors() throws Exception {
        this.mockMvc.perform(post("/admin/tasks/" + TASK_ID + "/update")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("title", " ")
                        .param("description", taskDto.getDescription())
                        .param("date", String.valueOf(LocalDate.now()))
                        .param("username", userDto.getUsername()))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attributeHasFieldErrors("taskDto", "title"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attributeExists("updateTaskId"));
    }

    @Test
    public void testCompleteTask_Success_RedirectToTasksPage() throws Exception {
        doNothing().when(taskService).changeTaskStatus(TASK_ID, TaskStatus.DONE);

        this.mockMvc.perform(post("/admin/tasks/" + TASK_ID + "/complete")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tasks"));

        verify(taskService, times(1)).changeTaskStatus(any(), any());
    }

    @Test
    public void testCancelTask_Success_RedirectToTasksPage() throws Exception {
        doNothing().when(taskService).changeTaskStatus(TASK_ID, TaskStatus.CANCELED);

        this.mockMvc.perform(post("/admin/tasks/" + TASK_ID + "/cancel")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tasks"));

        verify(taskService, times(1)).changeTaskStatus(any(), any());
    }

    @Test
    public void testDeleteTask_Success_RedirectToTasksPage() throws Exception {
        doNothing().when(taskService).deleteTaskById(TASK_ID);

        this.mockMvc.perform(post("/admin/tasks/" + TASK_ID + "/delete")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tasks"));

        verify(taskService, times(1)).deleteTaskById(any());
    }

    @Test
    public void testSuccessCreateNewTask_Success_RedirectToTasksPage() throws Exception {
        doNothing().when(taskService).saveNewTask(taskDto);

        this.mockMvc.perform(post("/admin/tasks/create")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("title", taskDto.getTitle())
                        .param("description", taskDto.getDescription())
                        .param("date", String.valueOf(LocalDate.now()))
                        .param("username", userDto.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tasks"));

        verify(taskService, times(1)).saveNewTask(any());
    }

    @Test
    public void testCreateNewTask_FailWithValidationErrors_ReturnFormWithErrors() throws Exception {
        this.mockMvc.perform(post("/admin/tasks/create")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("title", " ")
                        .param("description", "  ")
                        .param("date", String.valueOf(LocalDate.now()))
                        .param("username", userDto.getUsername()))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attributeHasFieldErrors("taskDto", "title", "description"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attributeExists("usernames"))
                .andExpect(model().attributeExists("hasErrors"));

        verify(taskService, times(0)).saveNewTask(any());
    }
}