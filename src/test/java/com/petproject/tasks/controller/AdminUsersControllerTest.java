package com.petproject.tasks.controller;

import com.petproject.tasks.config.SecurityConfig;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUsersController.class)
@Import(SecurityConfig.class)
public class AdminUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private List<UserDto> users;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        users = new ArrayList<>();

        userDto = UserDto.builder()
                .id(99L)
                .firstName("Mike")
                .username("admin123")
                .password("adm123")
                .roles(Set.of(UserRole.USER, UserRole.ADMIN))
                .build();
    }

    @Test
    public void testShowUserPage_Success_ReturnPage() throws Exception {
        when(userService.getFilteredUsers("", "")).thenReturn(users);

        this.mockMvc.perform(get("/admin/users")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("username", "")
                        .param("role", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("allRoles"))
                .andExpect(model().attributeExists("userDto"));

        verify(userService, times(1)).getFilteredUsers(any(), any());
    }

    @Test
    public void testCreateNewUser_Success_RedirectToUsersPage() throws Exception {
        User user = new User();

        when(userService.registerUser(userDto)).thenReturn(user);

        this.mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("firstName", userDto.getFirstName())
                        .param("username", userDto.getUsername())
                        .param("password", userDto.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    public void testCreateNewUser_FailWithValidationError_ReturnFormWithErrors() throws Exception {
        this.mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("firstName", userDto.getFirstName())
                        .param("username", "adm")
                        .param("password", userDto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeHasFieldErrors("userDto", "username"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("userDto"))
                .andExpect(model().attributeExists("hasErrors"));

        verify(userService, times(0)).registerUser(any(UserDto.class));
    }

    @Test
    public void testAddOrRemoveAdminRole_Success_RedirectToUsersPage() throws Exception {
        doNothing().when(userService).addOrRemoveAdminRole(userDto.getId());

        this.mockMvc.perform(post("/admin/users/" + userDto.getId())
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).addOrRemoveAdminRole(any());
    }

    @Test
    public void testDeleteUser_Success_RedirectToUsersPage() throws Exception {
        doNothing().when(userService).deleteUserByUserId(userDto.getId());

        this.mockMvc.perform(post("/admin/users/" + userDto.getId() + "/delete")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).deleteUserByUserId(any());
    }

    @Test
    public void testUpdateUser_Success_RedirectToUsersPage() throws Exception {
        doNothing().when(userService).updateUser(userDto.getId(), userDto);

        this.mockMvc.perform(post("/admin/users/" + userDto.getId() + "/update")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("firstName", "Leo")
                        .param("username", userDto.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).updateUser(any(), any(UserDto.class));
    }

    @Test
    public void testUpdateUser_FailWithValidationErrors_ReturnFormWithErrors() throws Exception {
        this.mockMvc.perform(post("/admin/users/" + userDto.getId() + "/update")
                        .with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name())))
                        .param("firstName", " ")
                        .param("username", userDto.getUsername())
                        .param("password", "222"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeHasFieldErrors("userDto", "firstName", "password"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("userDto"))
                .andExpect(model().attributeExists("updateUserId"));

        verify(userService, times(0)).updateUser(any(), any(UserDto.class));
    }
}