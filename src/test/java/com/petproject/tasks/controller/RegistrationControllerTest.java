package com.petproject.tasks.controller;

import com.petproject.tasks.config.SecurityConfig;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .firstName("Zia")
                .username("user123")
                .password("password123")
                .build();
    }

    @Test
    public void testShowRegistrationForm_Success_ReturnRegistrationPage() throws Exception {
        this.mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testRegisterUser_Success_RedirectToLoginPage() throws Exception {
        when(userService.existsUsername(userDto.getUsername())).thenReturn(false);

        this.mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", userDto.getFirstName())
                        .param("username", userDto.getUsername())
                        .param("password", userDto.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testRegisterUser_FailWithUsernameAlreadyExists_ReturnPageWithErrors() throws Exception {
        Mockito.when(userService.existsUsername(userDto.getUsername())).thenReturn(true);

        this.mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", userDto.getFirstName())
                        .param("username", userDto.getUsername())
                        .param("password", userDto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("register")) // Ожидаем, что останемся на странице регистрации
                .andExpect(model().attributeHasFieldErrors("user", "username")) // Ошибка у поля username
                .andExpect(model().attributeHasErrors("user"));
    }

    @Test
    public void testRegisterUser_FailValidationErrors_ReturnPageWithErrors() throws Exception {
        this.mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", " ")
                        .param("username", "us")
                        .param("password", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("user", "firstName", "username", "password"))
                .andExpect(model().attributeHasErrors("user"));
    }
}